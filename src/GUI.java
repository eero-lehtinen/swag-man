
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.ArrayList;

public class GUI {

    static Image[] numberImages = ImageImporter.getNumbers();
    static Image[] ghostPoints = ImageImporter.getGhostPointImages();
    static Image[] fruitPoints = ImageImporter.getFruitPointImages();
    static Image[] fruitImages = ImageImporter.getFruitImages();
    static Image extraLife = ImageImporter.getOneImage("extraLife.png");
    static Image oneUP = ImageImporter.getOneImage("1UP.png");
    static Image highScoreText = ImageImporter.getOneImage("highScore.png");
    static Image ready = ImageImporter.getOneImage("ready.png");
    static Image gameOver = ImageImporter.getOneImage("gameOver.png");
    static Image playerOne = ImageImporter.getOneImage("playerOne.png");

    static int length = 0;
    static String pointString;
    static int highScore;

    static void displayEatGhostPoints(boolean toState, int ghostCount) {
        displayEGP = toState;
        EGPImageIndex = ghostCount - 1;
    }

    private static boolean displayEGP;
    private static int EGPImageIndex;
    public static boolean displayReady;
    public static boolean displayGameOver;
    public static boolean displayPlayerOne;
    public static int drawLinearMasterVolumeTimer;

    static int fruitImageIndex = 0;

    static void paint(Graphics2D g2d) {

        drawScore(g2d, 5, Game.GL.points);

        if (Game.GL.points > highScore) {
            highScore = Game.GL.points;
        }

        if (highScore != 0) {
            drawScore(g2d, 15, highScore);
        }

        for (int i = 0; i < Game.GL.livesLeft - 1; i++) {
            g2d.drawImage(extraLife, (2 + i * 2) * 8, 34 * 8, null);
        }

        if (displayEGP) {
            g2d.drawImage(ghostPoints[EGPImageIndex], (int) Player.instance.x, (int) Player.instance.y + 4, null);
        }

        if (System.currentTimeMillis() % 600 > 300 || Game.GL.pauseAnim) {
            g2d.drawImage(oneUP, 3 * 8, 0, null);
        }

        g2d.drawImage(highScoreText, 9 * 8, 0, null);

        if (displayReady) {
            g2d.drawImage(ready, 88, 160, null);
        }

        if (displayGameOver) {
            g2d.drawImage(gameOver, 72, 160, null);
        }

        if (displayPlayerOne) {
            g2d.drawImage(playerOne, 72, 112, null);
        }

        if (Game.GL.fruitTimeRemaining > 0) {
            g2d.drawImage(fruitImages[fruitImageIndex], 13 * 8, 20 * 8 - 4, null);
        }

        if (displayFruitPoints) {
            g2d.drawImage(fruitPoints[Game.GL.getFruitValueIndex()], 12 * 8 + 4, 20 * 8, null);
        }

        if (drawLinearMasterVolumeTimer > 0) {
            drawLinearMasterVolumeTimer -= 10;
            g2d.drawImage(ImageImporter.getImageOfText("vol " + String.valueOf((int) (Main.audioManager.linearMasterVolume * 100)) + "%"), 20 * 8, 0, null);
        }

        drawLevelCounter(g2d);
    }

    static void drawScore(Graphics2D g2d, int xTile, int value) {
        if (value / 1000000f >= 1f) {
            value = (int) (value - (Math.floor(value / 1000000f) * 1000000));
        }
        pointString = String.valueOf(value / 10);
        length = pointString.length();

        int num = 0;
        for (int i = length; i > 0; i--) {
            num = Character.getNumericValue(pointString.charAt(i - 1));
            g2d.drawImage(numberImages[num], (xTile + i - length) * 8, 8, null);
        }

        g2d.drawImage(numberImages[0], (xTile + 1) * 8, 8, null);
    }

    static public List<Integer> levelCounterSpriteIndexes = new ArrayList<Integer>();
    static public boolean displayFruitPoints;

    static void drawLevelCounter(Graphics2D g2d) {
        for (int i = 0; i < levelCounterSpriteIndexes.size(); i++) {
            g2d.drawImage(fruitImages[levelCounterSpriteIndexes.get(i)], (24 - 2 * i) * 8, 34 * 8, null);
        }
    }

    static void updateLevel() {
        if (Game.GL.level >= 13) {
            fruitImageIndex = 7;
        } else if (Game.GL.level >= 11) {
            fruitImageIndex = 6;
        } else if (Game.GL.level >= 9) {
            fruitImageIndex = 5;
        } else if (Game.GL.level >= 7) {
            fruitImageIndex = 4;
        } else if (Game.GL.level >= 5) {
            fruitImageIndex = 3;
        } else if (Game.GL.level >= 3) {
            fruitImageIndex = 2;
        } else if (Game.GL.level == 2) {
            fruitImageIndex = 1;
        } else {
            fruitImageIndex = 0;
        }

        levelCounterSpriteIndexes.add(fruitImageIndex);

        if (levelCounterSpriteIndexes.size() > 7) {
            levelCounterSpriteIndexes.remove(0);
        }
    }

}
