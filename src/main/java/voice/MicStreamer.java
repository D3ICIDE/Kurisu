package voice;


import com.google.genai.AsyncSession;
import com.google.genai.types.Blob;
import com.google.genai.types.LiveSendRealtimeInputParameters;

import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;

/** Streams microphone audio to the Gemini Live session on a background thread. */
public final class MicStreamer implements AutoCloseable {

    private static final int CHUNK_BYTES = 3200; // ~100 ms at 16 kHz, 16-bit, mono

    private final TargetDataLine microphone;
    private final AsyncSession session;
    private volatile boolean running = true;
    private Thread thread;

    public MicStreamer(TargetDataLine microphone, AsyncSession session) {
        this.microphone = microphone;
        this.session = session;
    }

    public void start() {
        thread = Thread.ofPlatform().name("mic-streamer").daemon().start(this::streamLoop);
    }

    private void streamLoop() {
        byte[] buffer = new byte[CHUNK_BYTES];
        try {
            while (running && microphone.isOpen()) {
                int read = microphone.read(buffer, 0, buffer.length);
                if (read <= 0) {
                    continue;
                }
                Blob audio = Blob.builder()
                        .mimeType("audio/pcm;rate=16000")
                        .data(Arrays.copyOf(buffer, read))
                        .build();
                session.sendRealtimeInput(LiveSendRealtimeInputParameters.builder()
                        .audio(audio)
                        .build());
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("Error streaming mic audio: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
        microphone.stop();
        microphone.close();
    }
}

