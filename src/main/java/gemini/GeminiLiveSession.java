package gemini;

import com.google.genai.AsyncSession;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.LiveConnectConfig;
import com.google.genai.types.LiveSendToolResponseParameters;
import com.google.genai.types.Modality;
import com.google.genai.types.Part;
import com.google.genai.types.PrebuiltVoiceConfig;
import com.google.genai.types.SpeechConfig;
import com.google.genai.types.VoiceConfig;
import voice.MicStreamer;
import voice.SpeakerPlayer;

import javax.sound.sampled.TargetDataLine;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** Owns one Gemini Live voice session: connect, receive, stream the mic, shut down. */
public final class GeminiLiveSession implements AutoCloseable {

    private final String modelId;
    private final String voiceName;
    private final SpeakerPlayer speaker;
    private final TargetDataLine microphone;
    private final LiveToolRouter router;
    private final CountDownLatch done = new CountDownLatch(1);

    private AsyncSession session;
    private MicStreamer micStreamer;

    public GeminiLiveSession(String modelId,
                             String voiceName,
                             SpeakerPlayer speaker,
                             TargetDataLine microphone,
                             LiveToolRouter router) {
        this.modelId = modelId;
        this.voiceName = voiceName;
        this.speaker = speaker;
        this.microphone = microphone;
        this.router = router;
    }

    private LiveConnectConfig buildConfig() {
        SpeechConfig speech = SpeechConfig.builder()
                .voiceConfig(VoiceConfig.builder()
                        .prebuiltVoiceConfig(PrebuiltVoiceConfig.builder().voiceName(voiceName).build())
                        .build())
                .build();

        return LiveConnectConfig.builder()
                .responseModalities(List.of(new Modality(Modality.Known.AUDIO)))
                .speechConfig(speech)
                .systemInstruction(Content.builder()
                        .parts(List.of(Part.builder().text(LiveToolDeclarations.SYSTEM_INSTRUCTION).build()))
                        .build())
                .tools(List.of(LiveToolDeclarations.tools()))
                .build();
    }

    /** Connects and starts streaming. Returns immediately; use {@link #awaitEnd()} to block. */
    public void start() {
        System.out.println("Connecting to model " + modelId + " ...");
        Client client = new Client(); // reads GEMINI_API_KEY from the environment

        client.async.live.connect(modelId, buildConfig())
                .thenAccept(s -> {
                    this.session = s;
                    System.out.println("Connected! Speak into your microphone...");
                    s.receive(this::onMessage);
                    micStreamer = new MicStreamer(microphone, s);
                    micStreamer.start();
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to connect: " + ex.getMessage());
                    done.countDown();
                    return null;
                });
    }

    private void onMessage(com.google.genai.types.LiveServerMessage message) {
        message.serverContent().ifPresent(content -> {
            // Barge-in: the user talked over the model, so drop queued audio immediately.
            if (content.interrupted().orElse(false)) {
                speaker.interrupt();
            }
            content.modelTurn().flatMap(turn -> turn.parts()).ifPresent(parts ->
                    parts.forEach(part -> part.inlineData()
                            .flatMap(blob -> blob.data())
                            .ifPresent(speaker::play)));
        });

        message.toolCall().ifPresent(toolCall -> {
            var calls = toolCall.functionCalls().orElse(List.of());
            // Virtual thread keeps the receive callback free while the agents work.
            Thread.startVirtualThread(() -> {
                var responses = router.handle(calls);
                session.sendToolResponse(LiveSendToolResponseParameters.builder()
                        .functionResponses(responses)
                        .build());
                System.out.println("[Tool] Result sent back to Gemini.");
            });
        });
    }

    public void awaitEnd() throws InterruptedException {
        done.await();
    }

    @Override
    public void close() {
        done.countDown();
        if (micStreamer != null) {
            micStreamer.close();
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception ignored) {
                // already closed
            }
        }
    }
}

