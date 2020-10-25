
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

public class Sound {

    private Clip clip;
    //private String audioName;

    public Sound(String audioFile) {
        AudioInputStream audioStream = null;
        //audioName = audioFile;
        try {
            audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Game.class.getResourceAsStream(audioFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float dB) {
        FloatControl fControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fControl.setValue(dB);
    }

    public boolean isRunning() {
        return clip.isRunning();
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }

    public void play() {
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop() {
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
