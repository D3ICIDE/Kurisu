package voice;

import javax.sound.sampled.*;
import javax.xml.transform.Source;

public class AudioDeviceConfig {
    //Speakers Config
   public static final AudioFormat playbackFormat = new AudioFormat(24000.0f, 16, 1, true, false);
    public static final DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class,playbackFormat);

    //Microphone Config
    static AudioFormat micFormat = new AudioFormat(16000.0f, 16, 1, true, false);
    static DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, micFormat);



    public static SourceDataLine initSpeakers(){
        SourceDataLine speakers = null;
        try {
            speakers = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speakers.open(playbackFormat);
            speakers.start();
            System.out.println("Speaker system ready.");

        } catch (LineUnavailableException e) {
            System.out.println("Speakers are missing or not supported on your system");
        }
        return speakers;
    }

    public static TargetDataLine initMicrophone() {
        TargetDataLine microphone = null;
        try {
            microphone = (TargetDataLine) AudioSystem.getLine(micInfo);
            microphone.open(micFormat);
            microphone.start();
            System.out.println("Microphone system ready.");

        } catch (LineUnavailableException e) {
            System.out.println("Microphones Unavailable");
        }
        return microphone;
    }
}
