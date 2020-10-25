
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class Player {

    float x = 105f;
    float y = 205f;

    static BufferedImage[] pacmanSprites;
    static BufferedImage[] deathSprites;
    public static Player instance;

    public Player() {
        instance = this;
        oldTile = new Tile(1, 1, 1, BG.instance);
        pacmanSprites = ImageImporter.getPlayerImages();
        deathSprites = ImageImporter.getPlayerDeathImages();
    }

    int rotationIndex = 0;
    int oldRotationIndex = -1;
    int imageIndex = 2;

    boolean left = true, right, up, down;
    boolean moveLeft, moveRight, moveUp, moveDown;
    public boolean dead;
    boolean deathAnim;

    float speed = 0.64f;

    Tile oldTile;

    Tile curTile;

    Ghost ghost;

    void move() {

        if (dead) {
            return;
        }

        curTile = GetCurrentTile();

        if (oldTile != curTile) {

            if (curTile.tileType > 0 && curTile.tileType < 5) {
                Game.GL.eatPill(curTile);
            } else if (curTile.tileType == 10 || curTile.tileType == 11) {
                Game.GL.eatEnergizer(curTile);
            }

            if (Game.GL.fruitTimeRemaining > 0) {
                // If is over the fruits position
                if ((curTile == BG.instance.tiles[13][20] && oldTile == BG.instance.tiles[14][20]) || (oldTile == BG.instance.tiles[13][20] && curTile == BG.instance.tiles[14][20])) {
                    Game.GL.eatFruit();
                }
            }

            oldTile = curTile;

            speed = Game.GL.getPacSpeed();
        }

        ghost = whoGhostOnSameTile();

        if (!dead && ghost != null) {
            if (Game.GL.isFrightened(ghost)) {
                ghost.die();
                Main.audioManager.eatGhostClip.play();
            } else {
                die();
            }
        }

        if (left && curTile.canGoDir[0]) {
            moveLeft = true;
            moveRight = false;
            moveUp = false;
            moveDown = false;
        }
        if (right && curTile.canGoDir[1]) {
            moveRight = true;
            moveLeft = false;
            moveUp = false;
            moveDown = false;
        }
        if (up && curTile.canGoDir[2]) {
            moveUp = true;
            moveRight = false;
            moveLeft = false;
            moveDown = false;
        }
        if (down && curTile.canGoDir[3]) {
            moveDown = true;
            moveUp = false;
            moveRight = false;
            moveLeft = false;
        }

        if (moveUp || moveDown) {
            if (Math.abs(curTile.xPos - 3 - x) <= speed) {
                x = curTile.xPos - 3;
            } else if (curTile.xPos - 3 < x) {
                x -= speed;
            } else if (curTile.xPos - 3 > x) {
                x += speed;
            }
            imageIndex = Math.abs(Math.round((curTile.yPos - 3f - y) / 2f));
        }

        if (moveLeft || moveRight) {
            if (Math.abs(curTile.yPos - 3 - y) <= speed) {
                y = curTile.yPos - 3;
            } else if (curTile.yPos - 3 < y) {
                y -= speed;
            } else if (curTile.yPos - 3 > y) {
                y += speed;
            }
            imageIndex = Math.abs(Math.round((curTile.xPos - 3f - x) / 2f));
        }

        if (moveLeft) {
            if (curTile.canGoDir[0] || curTile.xPos - 3 < x) {
                x -= speed;
                rotationIndex = 0;
            }
        }

        if (moveRight) {
            if (curTile.canGoDir[1] || curTile.xPos - 3 > x) {
                x += speed;
                rotationIndex = 2;
            }
        }
        if (moveUp) {
            if (curTile.canGoDir[2] || curTile.yPos - 3 < y) {
                y -= speed;
                rotationIndex = 1;
            }
        }
        if (moveDown) {
            if (curTile.canGoDir[3] || curTile.yPos - 3 > y) {
                y += speed;
                rotationIndex = 3;
            }
        }

        if (curTile == BG.instance.leftTunnelTile1 && curTile.xPos - 3 > x) {
            x += 248;
        } else if (curTile == BG.instance.rightTunnelTile1 && curTile.xPos - 3 < x) {
            x -= 248;
        }

        if (oldRotationIndex != rotationIndex) {
            tx = AffineTransform.getRotateInstance(Math.PI / 2 * rotationIndex, 7.5, 7.5);
            op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        }
    }

    public void die() {
        dead = true;
        Game.canMove = false;
        Main.audioManager.stopAllLoops();
        die task = new die();
        (Game.instance.timers[3] = new Timer()).schedule(task, 1000);
    }

    private class die extends TimerTask {

        @Override
        public void run() {
            deathAnim = true;
            for (int i = 0; i < 4; i++) {
                Game.hideGhost[i] = true;
            }
            DeathAnim task = new DeathAnim();
            (Game.instance.timers[4] = new Timer()).scheduleAtFixedRate(task, 0, 1);
            Main.audioManager.deathClip.play();
        }
    };

    private class DeathAnim extends TimerTask {

        int i = 0;
        int num = 0;

        @Override
        public void run() {
            i++;
            num = (int) (i / 100);

            if (num < 11) {
                imageIndex = num;
            } else if (num == 20) {
                rotationIndex = 0;
                imageIndex = 2;
                dead = false;
                deathAnim = false;
                for (int i = 0; i < 4; i++) {
                    Game.hideGhost[i] = false;
                }
                moveLeft = false;
                moveDown = false;
                moveUp = false;
                moveRight = false;
                Game.GL.livesLeft--;
                if (Game.GL.livesLeft == 0) {
                    Game.instance.resetLevel(true, false);
                } else {
                    Game.instance.resetLevel(false, false);
                }
                Game.GL.pauseAnim = true;
                this.cancel();
            }
        }
    };

    Ghost whoGhostOnSameTile() {
        if (Game.instance.blinky.curTile == curTile && !Game.instance.blinky.dead) {
            return Game.instance.blinky;
        } else if (Game.instance.pinky.curTile == curTile && !Game.instance.pinky.dead) {
            return Game.instance.pinky;
        } else if (Game.instance.inky.curTile == curTile && !Game.instance.inky.dead) {
            return Game.instance.inky;
        } else if (Game.instance.clyde.curTile == curTile && !Game.instance.clyde.dead) {
            return Game.instance.clyde;
        } else {
            return null;
        }
    }

    Tile GetCurrentTile() {
        if (x + 7 >= 0 && x + 7 <= 224) {
            return BG.instance.tiles[(int) ((x + 7f) / 8f)][(int) ((y + 7f) / 8f)];
        } // If Peck-man is not in bunds get the end tunnel tiles
        else if (x + 7 < 0 && x + 7 > -8) {
            return BG.instance.leftTunnelTile0;
        } else if (x + 7 > 224 && x + 7 < 232) {
            return BG.instance.rightTunnelTile0;
        } else if (x + 7 < -8) {
            return BG.instance.leftTunnelTile1;
        } else {
            return BG.instance.rightTunnelTile1;
        }
    }

    AffineTransform tx;
    AffineTransformOp op;

    public void paint(Graphics2D g) {
        if (!deathAnim) {
            g.drawImage(op.filter(pacmanSprites[imageIndex], null), (int) x, (int) y, null);
        } else {
            g.drawImage(deathSprites[imageIndex], (int) x, (int) y, null);
        }
    }
}
