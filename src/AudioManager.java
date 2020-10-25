
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AudioManager {

    public AudioManager() {
        waka1 = new Sound("pacman_waka1.wav");
        waka2 = new Sound("pacman_waka2.wav");
        sirenClip = new Sound("pacman_siren.wav");
        beginningClip = new Sound("pacman_beginning.wav");
        frightenedClip = new Sound("pacman_frightened.wav");
        eatGhostClip = new Sound("pacman_eatghost.wav");
        ghostGoingHomeClip = new Sound("pacman_backToHome.wav");
        deathClip = new Sound("pacman_death.wav");
        eatFruit = new Sound("pacman_eatfruit.wav");
        extraLife = new Sound("pacman_extrapac.wav");
        SetLinearMasterVolume(0.5f, false);
    }

    private Sound waka1;
    private Sound waka2;
    Sound sirenClip;
    Sound beginningClip;
    Sound frightenedClip;
    Sound eatGhostClip;
    Sound ghostGoingHomeClip;
    Sound deathClip;
    Sound eatFruit;
    Sound extraLife;

    static int wakaIndex = 2;

    public void playWaka() {
        if (wakaIndex == 1) {
            waka1.play();
            wakaIndex = 2;
        } else {
            waka2.play();
            wakaIndex = 1;
        }
    }

    float linearMasterVolume = 0.5f;
    Float dB = 0f;

    public void SetLinearMasterVolume(float newVolume, boolean showGUI) {
        newVolume = round(newVolume, 2);

        if (newVolume <= 1f && newVolume >= 0f) {
            if (showGUI) {
                GUI.drawLinearMasterVolumeTimer = 1000;
            }

            linearMasterVolume = newVolume;
            dB = (float) (10 * Math.log10(Math.pow(linearMasterVolume, 4)) + 6f);

            beginningClip.setVolume(dB);
            sirenClip.setVolume(dB);
            frightenedClip.setVolume(dB);
            eatGhostClip.setVolume(dB);
            ghostGoingHomeClip.setVolume(dB);
            deathClip.setVolume(dB);
            eatFruit.setVolume(dB);
            extraLife.setVolume(dB);
            waka1.setVolume(dB);
            waka2.setVolume(dB);
        }
    }

    public float round(float value, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public void resumeSirenIfNeeded() {
        // If any ghost are dead, just do nothing
        // Else stop playing goHomeClip and play either siren or frightenedSiren
        boolean playSiren = true;
        for (int i = 0; i < Game.instance.ghostArray.length; i++) {
            if (Game.instance.ghostArray[i].dead) {
                playSiren = false;
            }
        }
        if (playSiren) {
            ghostGoingHomeClip.stop();
            if (Game.GL.frightenedTimer > 0) {
                frightenedClip.loop();
            } else {
                sirenClip.loop();
            }
        }
    }

    public void stopAllLoops() {
        frightenedClip.stop();
        sirenClip.stop();
        ghostGoingHomeClip.stop();
    }

    public void stopEverything() {
        waka1.stop();
        waka2.stop();
        sirenClip.stop();
        beginningClip.stop();
        frightenedClip.stop();
        eatGhostClip.stop();
        ghostGoingHomeClip.stop();
        deathClip.stop();
        eatFruit.stop();
        extraLife.stop();
    }
}
