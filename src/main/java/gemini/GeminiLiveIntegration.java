package gemini;

import core.Orchestrator;
import voice.AudioDeviceConfig;
import voice.SpeakerPlayer;

import javax.sound.sampled.TargetDataLine;

public class GeminiLiveIntegration {
    private static final String MODEL_ID = "gemini-3.8-live";
    private static final String VOICE = "Achernar"; // or "Kore", "Charon", "Fenrir", "Puck"
    private static final long TOOL_TIMEOUT_SECONDS = 45;
    private static final Orchestrator orchestrator = new Orchestrator();

    public static void main(String[] args) throws InterruptedException {
        SpeakerPlayer speaker = new SpeakerPlayer(AudioDeviceConfig.initSpeakers());
        TargetDataLine microphone = AudioDeviceConfig.initMicrophone();

        LiveToolRouter router = new LiveToolRouter(
                orchestrator::webSearch,
                orchestrator::runSupervisor,
                TOOL_TIMEOUT_SECONDS);

        GeminiLiveSession session = new GeminiLiveSession(MODEL_ID, VOICE, speaker, microphone, router);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            session.close();
            router.close();
            speaker.close();
        }));

        session.start();
        session.awaitEnd();

        session.close();
        router.close();
        speaker.close();
    }
}
