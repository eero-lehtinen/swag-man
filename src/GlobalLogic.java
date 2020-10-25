
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalLogic {

    public GlobalLogic() {
        (Game.instance.timers[5] = new Timer()).scheduleAtFixedRate(new Update(), 10, 100);
    }

    class Update extends TimerTask {

        @Override
        public void run() {
            if (Game.canMove) {
                ghostReleaseTimer += 0.1f;
                if (ghostReleaseTimer >= getGhostReleaseTimerTarget()) {
                    ghostReleaseTimer = 0;
                    releaseNextAvailableGhost();
                }
                if (fruitTimeRemaining > 0) {
                    fruitTimeRemaining -= 0.1;
                }
                if (frightenedTimer > 0) {
                    frightenedTimer -= 0.1f;
                    if (frightenedTimer <= 0) {
                        frightenedTimer = 0f;
                        Main.audioManager.frightenedClip.stop();
                        if (!Main.audioManager.ghostGoingHomeClip.isRunning()) {
                            Main.audioManager.sirenClip.loop();
                        }
                        for (int i = 0; i < 4; i++) {
                            ghostKilledWhileFright[i] = false;
                            ghostDeathCount = 0;
                        }
                    }
                } else {
                    scatterTimer += 0.1f;
                    if (scatterTimer >= getScatterTiming()) {
                        if (scatterCounter % 2 == 0) {
                            scatter = false;
                        } else {
                            scatter = true;
                        }

                        scatterTimer -= getScatterTiming();
                        scatterCounter++;

                        for (Ghost ghost : Game.instance.ghostArray) {
                            ghost.reverseDir = true;
                        }

                        // Just skip zeros
                        if (getScatterTiming() == 0) {
                            scatterCounter++;
                        }
                    }
                }
            }
        }
    };

    float ghostReleaseTimer = 0;
    float frightenedTimer = 0;
    int level = 1;
    public int points = 0;
    public int pillsCollected = 0;
    float scatterTimer;
    boolean scatter = true;
    int scatterCounter;
    float fruitTimeRemaining;

    boolean diedThisLevel;
    // is used after death
    int globalGhostPointCounter;

    public int livesLeft = 3;

    public boolean pauseAnim;

    public boolean[] ghostKilledWhileFright = new boolean[]{false, false, false, false};
    // Number of ghosts died this energization
    int ghostDeathCount = 0;

    boolean extraLifeGotten;

    public void eatPill(Tile tile) {
        Main.audioManager.playWaka();
        points += 10;
        pillsCollected++;
        tile.tileType += 5;
        Game.instance.skipFrames(1);
        ghostReleaseTimer = 0;

        if (244 - pillsCollected < getElroyPillsLeft(2)) {
            Game.instance.blinky.elroy = 2;
        } else if (244 - pillsCollected < getElroyPillsLeft(1)) {
            Game.instance.blinky.elroy = 1;
        }

        if (pillsCollected == 244) {
            nextLevel();
        }

        if (points >= 10000 && !extraLifeGotten) {
            Main.audioManager.extraLife.play();
            extraLifeGotten = true;
            livesLeft++;
        }

        incrementGhostPointCounter();
        spawnFruitIfNeeded();
    }

    public void eatEnergizer(Tile tile) {
        Main.audioManager.playWaka();
        points += 50;
        pillsCollected++;
        tile.tileType -= 4;
        Game.instance.skipFrames(3);
        ghostReleaseTimer = 0;

        Main.audioManager.sirenClip.stop();
        Main.audioManager.frightenedClip.loop();

        // If player died this game, blinky will become elroy when clyde leaves home
        if (!diedThisLevel) {
            if (244 - pillsCollected < getElroyPillsLeft(2)) {
                Game.instance.blinky.elroy = 2;
            } else if (244 - pillsCollected < getElroyPillsLeft(1)) {
                Game.instance.blinky.elroy = 1;
            }
        }

        // If frightTimer is 0, just change direction
        if (getFrightTime() != 0) {
            frightenedTimer = (float) getFrightTime();
        } else {
            for (int i = 0; i < 4; i++) {
                Game.instance.ghostArray[i].reverseDir = true;
            }
        }
        ghostDeathCount = 0;
        for (int i = 0; i < 4; i++) {
            ghostKilledWhileFright[i] = false;
        }

        if (pillsCollected == 244) {
            nextLevel();
        }

        if (points >= 10000 && !extraLifeGotten) {
            Main.audioManager.extraLife.play();
            extraLifeGotten = true;
            livesLeft++;
        }

        incrementGhostPointCounter();
        spawnFruitIfNeeded();
    }

    void incrementGhostPointCounter() {
        if (diedThisLevel) {
            globalGhostPointCounter++;
            if (globalGhostPointCounter == 7) {
                Game.instance.pinky.leaveHome = true;
            } else if (globalGhostPointCounter == 17) {
                Game.instance.inky.leaveHome = true;
            } else if (globalGhostPointCounter == 32) {
                Game.instance.clyde.leaveHome = true;

                if (244 - pillsCollected < getElroyPillsLeft(2)) {
                    Game.instance.blinky.elroy = 2;
                } else if (244 - pillsCollected < getElroyPillsLeft(1)) {
                    Game.instance.blinky.elroy = 1;
                }
            }
        } else {
            // If ghost is home and globalcounter isn't active, add points
            if (Game.instance.blinky.isHome) {
                Game.instance.blinky.pointCounter++;
            } else if (Game.instance.pinky.isHome) {
                Game.instance.pinky.pointCounter++;
            } else if (Game.instance.inky.isHome) {
                Game.instance.inky.pointCounter++;
            } else if (Game.instance.clyde.isHome) {
                Game.instance.clyde.pointCounter++;
            }
        }
    }

    public void spawnFruitIfNeeded() {
        if (pillsCollected == 70 || pillsCollected == 170) {
            fruitTimeRemaining = (float) (new Random().nextInt(11)) / 10 + 9f;
        }
    }

    public void eatFruit() {
        Main.audioManager.eatFruit.play();
        points += getFruitValue();
        fruitTimeRemaining = 0;
        GUI.displayFruitPoints = true;
        StopDisplayingFruitPoints task = new StopDisplayingFruitPoints();
        (Game.instance.timers[6] = new Timer()).schedule(task, 2000);
    }

    class StopDisplayingFruitPoints extends TimerTask {

        @Override
        public void run() {
            GUI.displayFruitPoints = false;
            this.cancel();
        }
    }

    public void nextLevel() {
        Main.audioManager.stopAllLoops();
        Game.canMove = false;
        (Game.instance.timers[7] = new Timer()).schedule(new HideGhosts_StartFlasingBG(), 2000);
    }

    class HideGhosts_StartFlasingBG extends TimerTask {

        @Override
        public void run() {
            for (int i = 0; i < Game.hideGhost.length; i++) {
                Game.hideGhost[i] = true;
            }
            BG.instance.img2 = ImageImporter.getOneImage("arena_white.png");
            BG.instance.startFlashing = true;
            (Game.instance.timers[8] = new Timer()).schedule(new StopFlashingBG_StartNewLevel(), 2000);
            this.cancel();
        }
    };

    class StopFlashingBG_StartNewLevel extends TimerTask {

        @Override
        public void run() {
            BG.instance.startFlashing = false;
            BG.instance.img2 = null;
            Game.instance.resetLevel(false, true);
            this.cancel();
        }
    };

    public float getPacSpeed() {
        int index = 0;

        // If is frightened, get frightenedspeed
        if (frightenedTimer > 0) {
            index = 1;
        }

        if (level == 1) {
            return pacManSpeed[index][0] * 0.8f;
        } else if (level >= 2 && level <= 4) {
            return pacManSpeed[index][1] * 0.8f;
        } else if (level >= 5 && level <= 20) {
            return pacManSpeed[index][2] * 0.8f;
        } else {
            return pacManSpeed[index][3] * 0.8f;
        }
    }

    public float getGhostSpeed(boolean frightened, boolean tunnel) {
        int index = 0;
        if (tunnel) {
            index = 2;
        } else if (frightened) {
            index = 1;
        }

        if (level == 1) {
            return ghostSpeed[index][0] * 0.8f;
        } else if (level >= 2 && level <= 4) {
            return ghostSpeed[index][1] * 0.8f;
        } else if (level >= 5 && level <= 20) {
            return ghostSpeed[index][2] * 0.8f;
        } else {
            return ghostSpeed[index][3] * 0.8f;
        }
    }

    public long getFrightTime() {
        if (level <= 21) {
            return frightenedTime[level];
        } else {
            return frightenedTime[20];
        }
    }

    boolean isFrightened(Ghost ghost) {
        if (!ghost.dead && !ghostKilledWhileFright[getGhostNumber(ghost.ghostType)] && frightenedTimer > 0f) {
            return true;
        } else {
            return false;
        }
    }

    void eatGhost(Ghost.GhostType gt) {
        ghostKilledWhileFright[getGhostNumber(gt)] = true;
        ghostDeathCount++;
        points += Math.pow(2, ghostDeathCount) * 100;

        Game.canMove = false;
        Game.hidePlayer = true;
        Game.hideGhost[getGhostNumber(gt)] = true;
        GUI.displayEatGhostPoints(true, ghostDeathCount);
        endEatGhostPause task = new endEatGhostPause();
        Main.audioManager.eatGhostClip.play();
        task.ghostNum = getGhostNumber(gt);
        (Game.instance.timers[9] = new Timer()).schedule(task, 1000);
    }

    private class endEatGhostPause extends TimerTask {

        int ghostNum;

        @Override
        public void run() {
            Game.canMove = true;
            Game.hidePlayer = false;
            Game.hideGhost[ghostNum] = false;
            GUI.displayEatGhostPoints(false, ghostDeathCount);
            Main.audioManager.frightenedClip.stop();
            Main.audioManager.ghostGoingHomeClip.loop();
            this.cancel();
        }
    };

    public int getGhostNumber(Ghost.GhostType gt) {
        if (gt == Ghost.GhostType.Blinky) {
            return 0;
        } else if (gt == Ghost.GhostType.Pinky) {
            return 1;
        } else if (gt == Ghost.GhostType.Inky) {
            return 2;
        } else {
            return 3;
        }
    }

    final private int[] frightenedTime = new int[]{6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1, 0, 0, 0};

    // Indexes
    // first index 0 = normal, 1 = frightened, 2 = tunnel for ghosts
    // Second indexes:
    // 0 = level 1
    // 1 = levels 2-4
    // 2 = levels 5-20
    // 3 = levels 20+
    final private float[][] ghostSpeed = new float[][]{{0.75f, 0.85f, 0.95f, 0.95f}, {0.5f, 0.55f, 0.6f, 0.95f}, {0.4f, 0.45f, 0.5f, 0.5f}};
    final private float[][] pacManSpeed = new float[][]{{0.8f, 0.9f, 1f, 0.9f}, {0.9f, 0.95f, 1f, 0.9f}};

    float getElroySpeed(int firstOrSecond) {
        if (level == 1) {
            return blinkyElroySpeed[firstOrSecond - 1][0] * 0.8f;
        } else if (level >= 2 && level <= 4) {
            return blinkyElroySpeed[firstOrSecond - 1][1] * 0.8f;
        } else {
            return blinkyElroySpeed[firstOrSecond - 1][2] * 0.8f;
        }
    }

    // first index: 0 = first elroy, 1 = second elroy
    // second index: 0 = level 1, 1 = levels 2-4, 2 = levels 5+
    final private float[][] blinkyElroySpeed
            = {{0.8f, 0.9f, 1f}, {0.85f, 0.95f, 1.05f}};

    // first index: 0 = first elroy, 1 = second elroy
    // second index: 
    // 0 = level 1, 
    // 1 = level 2
    // 2 = levels 3-5
    // 3 = levels 6-8
    // 4 = levels 9-11
    // 5 = levels 12-14
    // 6 = levels 15-18
    // 7 = levels 19+
    final private int[][] blinkyElroyPillsLeft
            = {{20, 30, 40, 50, 60, 80, 100, 120}, {10, 15, 20, 25, 30, 40, 50, 60}};

    float getElroyPillsLeft(int firstOrSecond) {
        if (level == 1) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][0];
        } else if (level == 2) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][1];
        } else if (level >= 3 && level <= 5) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][2];
        } else if (level >= 6 && level <= 8) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][3];
        } else if (level >= 9 && level <= 11) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][4];
        } else if (level >= 12 && level <= 14) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][5];
        } else if (level >= 15 && level <= 18) {
            return blinkyElroyPillsLeft[firstOrSecond - 1][6];
        } else {
            return blinkyElroyPillsLeft[firstOrSecond - 1][7];
        }
    }
    // Indexes
    // first index:
    // 0 = level 1
    // 1 = levels = 4-2
    // 2 = levels = 5+
    // secon index:
    // uneven numbers are chase (it starts from zero)
    // even numbers are scatter
    final private float[][] scatterTiming = new float[][]{{7f, 20f, 7f, 20f, 5f, 20f, 5f, Float.POSITIVE_INFINITY},
    {7f, 20f, 7f, 20f, 5f, 1033f, 0f, Float.POSITIVE_INFINITY},
    {5f, 20f, 5f, 20f, 5f, 1037f, 0f, Float.POSITIVE_INFINITY}};

    private float getScatterTiming() {
        if (level == 1) {
            return scatterTiming[0][scatterCounter];
        } else if (level >= 2 && level <= 4) {
            return scatterTiming[1][scatterCounter];
        } else {
            return scatterTiming[2][scatterCounter];
        }
    }

    // What is the x-coordinate of all ghosts
    // Indexes: 0 = Blinky, 1 = Pinky, 2 = Inky, 3 = Clyde
    final public int[] homePosX = new int[]{13 * 8, 13 * 8, 11 * 8, 15 * 8};

    // How many points does ghost need to get to get out of home
    // First indexes: 0 = level 1, 1 = level 2, 2 = levels 3+
    // Second indexes: ghosts in order
    final public int[][] ghostPointTarget = {{0, 0, 30, 60}, {0, 0, 0, 50}, {0, 0, 0, 0}};

    public int getGhostPointTarget(Ghost.GhostType gt) {
        if (level == 1) {
            return ghostPointTarget[0][getGhostNumber(gt)];
        } else if (level == 2) {
            return ghostPointTarget[1][getGhostNumber(gt)];
        } else {
            return 0;
        }
    }

    final float[] ghostReleaseTimerTarget = {4, 3};

    float getGhostReleaseTimerTarget() {
        if (level < 5) {
            return ghostReleaseTimerTarget[0];
        } else {
            return ghostReleaseTimerTarget[1];
        }
    }

    void releaseNextAvailableGhost() {
        if (Game.instance.blinky.isHome) {
            Game.instance.blinky.leaveHome = true;
        } else if (Game.instance.pinky.isHome) {
            Game.instance.pinky.leaveHome = true;
        } else if (Game.instance.inky.isHome) {
            Game.instance.inky.leaveHome = true;
        } else if (Game.instance.clyde.isHome) {
            Game.instance.clyde.leaveHome = true;
        }
    }

    final public int[] fruitValues = {100, 300, 500, 700, 1000, 2000, 3000, 5000};

    int getFruitValue() {
        if (level >= 13) {
            return fruitValues[7];
        } else if (level >= 11) {
            return fruitValues[6];
        } else if (level >= 9) {
            return fruitValues[5];
        } else if (level >= 7) {
            return fruitValues[4];
        } else if (level >= 5) {
            return fruitValues[3];
        } else if (level >= 3) {
            return fruitValues[2];
        } else if (level == 2) {
            return fruitValues[1];
        } else {
            return fruitValues[0];
        }
    }

    int getFruitValueIndex() {
        if (level >= 13) {
            return 7;
        } else if (level >= 11) {
            return 6;
        } else if (level >= 9) {
            return 5;
        } else if (level >= 7) {
            return 4;
        } else if (level >= 5) {
            return 3;
        } else if (level >= 3) {
            return 2;
        } else if (level == 2) {
            return 1;
        } else {
            return 0;
        }
    }
}
