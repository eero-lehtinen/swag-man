
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Game extends JPanel implements KeyListener {

    static GlobalLogic GL;
    public static boolean canMove = false;

    BG bg;
    Player player;
    public Ghost blinky;
    public Ghost pinky;
    public Ghost inky;
    public Ghost clyde;
    public Ghost[] ghostArray;
    Image img = null;

    static public Game instance;

    public Game() {
        repaint();
        instance = this;

        GL = new GlobalLogic();
        bg = new BG();
        GUI.updateLevel();
        player = new Player();
        blinky = new Ghost(Ghost.GhostType.Blinky);
        pinky = new Ghost(Ghost.GhostType.Pinky);
        inky = new Ghost(Ghost.GhostType.Inky);
        clyde = new Ghost(Ghost.GhostType.Clyde);
        ghostArray = new Ghost[]{blinky, pinky, inky, clyde};

        addKeyListener(this);

        playerMove();
        ghostMove();

        GL.pauseAnim = true;
        GUI.displayReady = true;

        (timers[0] = new Timer()).scheduleAtFixedRate(new UpdateFrame(), 10, 10);

        GUI.displayPlayerOne = true;

        for (int i = 0; i < 4; i++) {
            hideGhost[i] = true;
        }
        hidePlayer = true;
        timers[0].schedule(new hidePlayerOneText(), 2000);

        Main.audioManager.beginningClip.play();
    }

    private void playerMove() {
        player.move();
    }

    private void ghostMove() {
        blinky.update();
        pinky.update();
        inky.update();
        clyde.update();
    }

    public static boolean[] hideGhost = new boolean[4];
    public static boolean hidePlayer;

    float[] matrix = {
        0.111111f, 0.111111f, 0.111111f,
        0.111111f, 0.111111f, 0.111111f,
        0.111111f, 0.111111f, 0.111111f};
    BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
    static BufferedImage originalImg = new BufferedImage(224 * Main.scaleFactor, 288 * Main.scaleFactor, BufferedImage.TYPE_INT_RGB);
    boolean blur = false;
    Graphics2D g2d;
    Graphics2D g2d2;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (blur) {
            g2d = originalImg.createGraphics();
        } else {
            g2d = (Graphics2D) g;
        }

        g2d.scale(Main.scaleFactor, Main.scaleFactor);
        bg.paint(g2d);
        GUI.paint(g2d);
        if (!hidePlayer) {
            player.paint(g2d);
        }
        if (!hideGhost[0]) {
            blinky.paint(g2d);
        }
        if (!hideGhost[1]) {
            pinky.paint(g2d);
        }
        if (!hideGhost[2]) {
            inky.paint(g2d);
        }
        if (!hideGhost[3]) {
            clyde.paint(g2d);
        }

        if (blur) {
            g2d2 = (Graphics2D) g;
            g2d2.drawImage(op.filter(originalImg, null), 0, 0, null);
        }
    }

    static int pacmanStopFrames = 0;
    static float stopFrameOverflow = 0f;

    public void Update() {

        if (canMove) {
            if (pacmanStopFrames == 0) {
                playerMove();
            } else {
                pacmanStopFrames--;
            }

            ghostMove();
        }
        repaint();
    }

    public void skipFrames(int amount) {
        double tempValue = amount * 1.6666666;
        pacmanStopFrames = (int) Math.floor(tempValue);
        stopFrameOverflow += tempValue - pacmanStopFrames;
        if (stopFrameOverflow >= 1f) {
            pacmanStopFrames++;
            stopFrameOverflow -= 1f;
        }
    }

    class UpdateFrame extends TimerTask {

        public void run() {
            requestFocusInWindow();
            Update();
        }
    };

    public void resetLevel(boolean completeReset, boolean nextLevel) {

        for (Ghost ghost : ghostArray) {
            ghost.leaveHome = false;
            ghost.isHome = true;
            ghost.homeDir = 0;
            ghost.curDir = 0;
            ghost.x = ghost.homePos.x;
            ghost.y = ghost.homePos.y;
            ghost.pointCounter = 0;
            ghost.rotationIndex = 0;
            ghost.oldTile = new Tile(-9, -9);
            ghost.curTile = new Tile(-10, -10);
            ghost.directionFound = false;
            ghost.targetCoord = new IntCoord(0, 0);
            ghost.goIntoHome = false;
            ghost.homeGoUp = false;
            ghost.dead = false;
            ghost.wasFrightened = false;
            ghost.reverseDir = false;
            ghost.speed = GL.getGhostSpeed(false, false);
            ghost.elroy = 0;
        }

        GL.frightenedTimer = 0;
        GL.scatterCounter = 0;
        GL.scatterTimer = 0f;
        GL.scatter = true;
        GL.globalGhostPointCounter = 0;
        GL.fruitTimeRemaining = 0f;

        blinky.x = 105f;
        blinky.y = 108f;
        blinky.isHome = false;
        blinky.curDir = 0;
        blinky.rotationIndex = 2;
        pinky.homeDir = 1;
        pinky.rotationIndex = 1;

        player.x = 105f;
        player.y = 205f;

        if (completeReset) {
            GL.diedThisLevel = false;
            GUI.displayGameOver = true;
            for (int i = 0; i < 4; i++) {
                Game.hideGhost[i] = true;
            }
            hidePlayer = true;
            (timers[1] = new Timer()).schedule(new startOver(), 2000);
        } else if (nextLevel) {
            GL.diedThisLevel = false;
            for (int i = 0; i < 4; i++) {
                Game.hideGhost[i] = true;
            }
            hidePlayer = true;
            startOver task = new startOver();
            task.justChangeLevel = true;
            (timers[1] = new Timer()).schedule(task, 2000);
        } else {
            GL.diedThisLevel = true;
            GUI.displayReady = true;
            (timers[1] = new Timer()).schedule(new start(), 2000);
        }
    }

    private class startOver extends TimerTask {

        boolean justChangeLevel;

        @Override
        public void run() {
            GL.ghostReleaseTimer = 0;
            GL.pillsCollected = 0;
            blinky.elroy = 0;
            GUI.displayReady = true;
            bg.refillTiles();

            if (justChangeLevel) {
                GL.level++;
                GUI.updateLevel();
                (timers[2] = new Timer()).schedule(new start(), 2000);
                for (int i = 0; i < 4; i++) {
                    Game.hideGhost[i] = false;
                }
                hidePlayer = false;
            } else {
                GL.level = 1;
                GL.points = 0;
                GL.livesLeft = 3;
                GUI.displayGameOver = false;
                GUI.levelCounterSpriteIndexes.clear();
                GUI.updateLevel();
                GL.extraLifeGotten = false;

                //ShowPlayerOneText
                GUI.displayPlayerOne = true;

                (timers[2] = new Timer()).schedule(new hidePlayerOneText(), 2000);

                Main.audioManager.beginningClip.play();
            }
        }
    };

    public class hidePlayerOneText extends TimerTask {

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                Game.hideGhost[i] = false;
            }
            hidePlayer = false;
            GUI.displayPlayerOne = false;

            (timers[3] = new Timer()).schedule(new start(), 2000);
        }
    };

    public class start extends TimerTask {

        @Override
        public void run() {
            canMove = true;
            player.left = true;
            GL.pauseAnim = false;
            GUI.displayReady = false;
            Main.audioManager.sirenClip.loop();
        }
    };

    Timer[] timers = new Timer[10];

    public void cancelAllTimers() {
        for (int i = 0; i < timers.length; i++) {
            timers[i].cancel();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = true;
            player.right = false;
            player.up = false;
            player.down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = true;
            player.left = false;
            player.up = false;
            player.down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = true;
            player.down = false;
            player.left = false;
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = true;
            player.up = false;
            player.left = false;
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
            Main.audioManager.SetLinearMasterVolume(Main.audioManager.linearMasterVolume + 0.05f, true);
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            Main.audioManager.SetLinearMasterVolume(Main.audioManager.linearMasterVolume - 0.05f, true);
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_F) {
            for (Ghost ghost : ghostArray)
                ghost.showTarget = !ghost.showTarget;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            player.left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            player.up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            player.down = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
