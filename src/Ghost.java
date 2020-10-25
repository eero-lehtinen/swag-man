
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;

public class Ghost {

    public Ghost(GhostType gt) {
        ghostType = gt;
        speed = Game.GL.getGhostSpeed(false, false);
        images = ImageImporter.getGhostImages(gt);

        if (gt == GhostType.Blinky) {
            color = new Color(1f, 0f, 0f);
            scatterTarget = new IntCoord(25 * 8, 0);
            homePos.x = Game.GL.homePosX[0];
            rotationIndex = 2;
        } else if (gt == GhostType.Pinky) {
            color = new Color(1f, 0.69f, 0.85f);
            scatterTarget = new IntCoord(2 * 8, 0);
            isHome = true;
            homePos.x = Game.GL.homePosX[1];
            rotationIndex = 1;
        } else if (gt == GhostType.Inky) {
            color = new Color(0f, 1f, 0.85f);
            scatterTarget = new IntCoord(27 * 8, 35 * 8);
            isHome = true;
            homePos.x = Game.GL.homePosX[2];
        } else if (gt == GhostType.Clyde) {
            color = new Color(1f, 0.69f, 0.24f);
            scatterTarget = new IntCoord(0, 35 * 8);
            isHome = true;
            homePos.x = Game.GL.homePosX[3];
            ;
        }
        if (gt != GhostType.Blinky) {
            x = homePos.x;
            y = homePos.y;
        }
    }

    float x = 105f;
    float y = 108f;
    float speed = 0.6f;
    Image[][] images;
    int rotationIndex;
    Color color;

    public IntCoord homePos = new IntCoord(0, (16 * 8) + 4);

    public int pointCounter = 0;
    public int pointCounterTarget = 0;

    boolean leaveHome;

    enum GhostType {
        Blinky, Pinky, Inky, Clyde
    }

    GhostType ghostType;

    int curDir = 0;

    Tile curTile;
    Tile oldTile;
    boolean directionFound;
    IntCoord targetCoord = new IntCoord(0, 0);
    IntCoord scatterTarget;

    // rotationIndexes:
    // 0 = up
    // 1 = down
    // 2 = left
    // 3 = right
    public boolean isHome;
    boolean goIntoHome = false;
    int homeDir;
    boolean homeGoUp;
    boolean dead;
    boolean wasFrightened;
    boolean reverseDir;

    int elroy = 0;

    boolean showTarget;

    void update() {

        curTile = GetCurrentTile();

        if (isHome) {
            HomeStuff();
            return;
        } else if (goIntoHome) {
            goIntoHome();
            return;
        }

        if (oldTile != curTile) {

            directionFound = false;

            if (dead && ((curTile == BG.instance.tiles[13][14] && oldTile == BG.instance.tiles[14][14])
                    || (oldTile == BG.instance.tiles[13][14] && curTile == BG.instance.tiles[14][14]))) {
                goIntoHome = true;
            }

            if (reverseDir) {
                reverseDir = false;
                curDir = GetOppositeDir(curDir);
                directionFound = true;
            }

            oldTile = curTile;

            if (!wasFrightened && Game.GL.isFrightened(this)) {
                curDir = GetOppositeDir(curDir);
                directionFound = true;
                wasFrightened = true;
            } else if (!Game.GL.isFrightened(this)) {
                wasFrightened = false;
            }

            if (dead) {
                speed = Game.GL.getGhostSpeed(false, false) * 1.5f;
            } else if (curTile.tileType == 5) {
                speed = Game.GL.getGhostSpeed(false, true);
            } else if (Game.GL.isFrightened(this)) {
                speed = Game.GL.getGhostSpeed(true, false);
            } else if (elroy == 1 && ghostType == GhostType.Blinky) {
                speed = Game.GL.getElroySpeed(1);
            } else if (elroy == 2 && ghostType == GhostType.Blinky) {
                speed = Game.GL.getElroySpeed(2);
            } else {
                speed = Game.GL.getGhostSpeed(false, false);
            }
        }

        Movement();

        // Move to the other tunnel's end
        if (curTile == BG.instance.leftTunnelTile1 && curTile.xPos - 3 > x) {
            x += 248;
        } else if (curTile == BG.instance.rightTunnelTile1 && curTile.xPos - 3 < x) {
            x -= 248;
        }

    }

    Tile GetCurrentTile() {
        if (x + 8 >= 0 && x + 8 <= 224) {
            return BG.instance.tiles[(int) ((x + 8f) / 8f)][(int) ((y + 8f) / 8f)];
        } // If Peck-man is not in bunds get the end tunnel tiles
        else if (x + 8 < 0 && x + 8 > -8) {
            return BG.instance.leftTunnelTile0;
        } else if (x + 8 > 224 && x + 8 < 232) {
            return BG.instance.rightTunnelTile0;
        } else if (x + 8 < -8) {
            return BG.instance.leftTunnelTile1;
        } else {
            return BG.instance.rightTunnelTile1;
        }
    }

    void Movement() {
        if (curDir == 0) {
            if ((curTile.canGoDir[0] && (curTile.tileType == 1 || curTile.tileType == 6 || curTile.tileType == 5
                    || curTile.tileType == 10 || curTile.tileType == 11)) || directionFound) {
                x -= speed;
                rotationIndex = 2;
            } else if (curTile.xPos - 4 < x) {
                x -= speed;
                rotationIndex = 2;
                if (curTile.xPos - 4 >= x) {
                    curDir = GetNewDirection();
                    directionFound = true;
                    if (curDir != 0) {
                        x = curTile.xPos - 4;
                    }
                }
            }
        } else if (curDir == 1) {
            if ((curTile.canGoDir[1] && (curTile.tileType == 1 || curTile.tileType == 6 || curTile.tileType == 5
                    || curTile.tileType == 10 || curTile.tileType == 11)) || directionFound) {
                x += speed;
                rotationIndex = 3;
            } else if (curTile.xPos - 4 > x) {
                x += speed;
                rotationIndex = 3;
                if (curTile.xPos - 4 <= x) {
                    curDir = GetNewDirection();
                    directionFound = true;
                    if (curDir != 1) {
                        x = curTile.xPos - 4;
                    }

                }
            }
        } else if (curDir == 2) {
            if ((curTile.canGoDir[2] && (curTile.tileType == 1 || curTile.tileType == 6 || curTile.tileType == 5
                    || curTile.tileType == 10 || curTile.tileType == 11)) || directionFound) {
                y -= speed;
                rotationIndex = 0;
            } else if (curTile.yPos - 4 < y) {
                y -= speed;
                rotationIndex = 0;
                if (curTile.yPos - 4 >= y) {
                    curDir = GetNewDirection();
                    directionFound = true;
                    if (curDir != 2) {
                        y = curTile.yPos - 4;
                    }
                }
            }
        } else if (curDir == 3) {
            if ((curTile.canGoDir[3] && (curTile.tileType == 1 || curTile.tileType == 6 || curTile.tileType == 5
                    || curTile.tileType == 10 || curTile.tileType == 11)) || directionFound) {
                y += speed;
                x = curTile.xPos - 4;
                rotationIndex = 1;
            } else if (curTile.yPos - 4 > y) {
                y += speed;
                rotationIndex = 1;
                if (curTile.yPos - 4 <= y) {
                    curDir = GetNewDirection();
                    directionFound = true;
                    if (curDir != 3) {
                        y = curTile.yPos - 4;
                    }
                }
            }
        }
    }

    void HomeStuff() {
        if ((pointCounter >= Game.GL.getGhostPointTarget(ghostType) && !Game.GL.diedThisLevel) || leaveHome
                || ghostType == GhostType.Blinky) {
            if (!homeGoUp) {
                if (x < 13 * 8) {
                    rotationIndex = 3;
                    x += speed / 2;
                    if (x >= 13 * 8) {
                        x = 13 * 8;
                        homeGoUp = true;
                    }
                } else if (x > 13 * 8) {
                    rotationIndex = 2;
                    x -= speed / 2;
                    if (x <= 13 * 8) {
                        x = 13 * 8;
                        homeGoUp = true;
                    }
                } else {
                    homeGoUp = true;
                }
                y = homePos.y;
            } else {
                if (y > 14 * 8 - 4) {
                    rotationIndex = 0;
                    y -= speed / 2;
                } else {
                    isHome = false;
                    leaveHome = false;
                    homeGoUp = false;
                    curDir = 0;
                }
            }
        } else {
            if (homeDir == 0 && y > homePos.x - 4) {
                y -= speed / 2;
                rotationIndex = 0;
                x = homePos.x;
                if (y <= homePos.y - 4) {
                    homeDir = 1;
                }
            } else if (homeDir == 1 && y < homePos.y + 4) {
                y += speed / 2;
                rotationIndex = 1;
                x = homePos.x;
                if (y >= homePos.y + 4) {
                    homeDir = 0;
                }
            }
        }
    }

    void goIntoHome() {
        if (y < homePos.y) {
            rotationIndex = 1;
            y += speed;
        } else if (x < homePos.x) {
            x += speed;
            rotationIndex = 3;
            if (x >= homePos.x) {
                goIntoHome = false;
                dead = false;
                isHome = true;
                Main.audioManager.resumeSirenIfNeeded();
            }
        } else if (x > homePos.x) {
            x -= speed;
            rotationIndex = 2;
            if (x <= homePos.x) {
                goIntoHome = false;
                dead = false;
                isHome = true;
                Main.audioManager.resumeSirenIfNeeded();
            }
        }
    }

    // Ghost's preferences in order
    int[] dirPref = new int[] { 2, 0, 3, 1 };

    int GetNewDirection() {
        // Jos on nurkka, menee sinne mihin vain pystyy
        if (curTile.tileType == 2 || curTile.tileType == 7 || curTile.tileType == 11) {
            return GetNewCornerDir();
        }

        if (Game.GL.isFrightened(this)) {
            // Random suunta ja jos siellä on seinä, niin menee preferenssien mukaan
            Random random = new Random();
            int randomInt = random.nextInt(4);
            if (curTile.canGoDir[randomInt] && randomInt != GetOppositeDir(curDir)) {
                return randomInt;
            } else {
                for (int i = 0; i < 4; i++) {
                    if (curTile.canGoDir[dirPref[i]] && dirPref[i] != GetOppositeDir(curDir)) {
                        return dirPref[i];
                    }
                }
            }
        }

        // Erikoisristeys (ei saa kääntyä ylös)! Jos ei mene alas niin jatkaa matkaa
        // suoraan. Ei vaikuta kuolleisiin
        if (curDir != 3 && (curTile.tileType == 4 || curTile.tileType == 9) && !dead) {
            // Älä tee mitään koska jatka suoraan
            return curDir;
        }

        targetCoord = dead ? homePos : GetMyTargetCoord();

        double shortestDistance = Double.MAX_VALUE;
        double tempDistance = 0;
        int bestDir = 0;

        for (int i = 0; i < 4; i++) {

            // Can't turn backwards
            if (dirPref[i] != GetOppositeDir(curDir) && curTile.canGoDir[dirPref[i]]) {

                Tile CrossAdjTile = GetAdjacentTile(dirPref[i], curTile);
                tempDistance = GetDistance(CrossAdjTile.xPos, targetCoord.x, CrossAdjTile.yPos, targetCoord.y);

                if (shortestDistance > tempDistance) {
                    shortestDistance = tempDistance;
                    bestDir = dirPref[i];
                }

            }
        }
        return bestDir;
    }

    IntCoord GetMyTargetCoord() {
        if (Game.GL.scatter && elroy == 0) {
            return scatterTarget;
        }

        switch (ghostType) {
            case Blinky:
                return GetBlinkyTargetCoord();
            case Pinky:
                return GetPinkyTargetCoord();
            case Inky:
                return GetInkyTargetCoord();
            case Clyde:
                return GetClydeTargetCoord();
            default:
                System.err.println("GhostType not set!");
                return new IntCoord(0, 0);
        }
    }

    IntCoord GetBlinkyTargetCoord() {
        if (Game.GL.scatter && elroy == 0) {
            return scatterTarget;
        }

        return new IntCoord(Game.instance.player.curTile.xPos, Game.instance.player.curTile.yPos);
    }

    IntCoord GetPinkyTargetCoord() {
        if (Game.GL.scatter) {
            return scatterTarget;
        }

        if (Game.instance.player.moveLeft) {
            return new IntCoord(Game.instance.player.curTile.xPos - 32, Game.instance.player.curTile.yPos);
        } else if (Game.instance.player.moveDown) {
            return new IntCoord(Game.instance.player.curTile.xPos, Game.instance.player.curTile.yPos + 32);
        } else if (Game.instance.player.moveUp) {
            return new IntCoord(Game.instance.player.curTile.xPos, Game.instance.player.curTile.yPos - 32);
        } else {
            return new IntCoord(Game.instance.player.curTile.xPos + 32, Game.instance.player.curTile.yPos);
        }

    }

    IntCoord GetInkyTargetCoord() {
        if (Game.GL.scatter) {
            return scatterTarget;
        }

        IntCoord playerTilePos = new IntCoord(Game.instance.player.curTile.xPos, Game.instance.player.curTile.yPos);

        // Set offset to two tiles in front of pacman
        if (Game.instance.player.rotationIndex == 0) {
            playerTilePos.x -= 16;
        } else if (Game.instance.player.rotationIndex == 3) {
            playerTilePos.y += 16;
        } else if (Game.instance.player.rotationIndex == 2) {
            playerTilePos.y -= 16;
        } else {
            playerTilePos.x += 16;
        }

        return new IntCoord(playerTilePos.x + (playerTilePos.x - Game.instance.blinky.curTile.xPos),
                playerTilePos.y + (playerTilePos.y - Game.instance.blinky.curTile.yPos));
    }

    IntCoord GetClydeTargetCoord() {
        if (Game.GL.scatter) {
            return scatterTarget;
        }

        if (GetDistance(curTile.xPos, Game.instance.player.curTile.xPos, curTile.yPos,
                Game.instance.player.curTile.yPos) > 64) {
            return new IntCoord(Game.instance.player.curTile.xPos, Game.instance.player.curTile.yPos);
        } else {
            return scatterTarget;
        }
    }

    Tile GetAdjacentTile(int dir, Tile tile) {
        switch (dir) {
            case 0:
                return BG.instance.tiles[tile.xPos / 8 - 1][tile.yPos / 8];
            case 1:
                return BG.instance.tiles[tile.xPos / 8 + 1][tile.yPos / 8];
            case 2:
                return BG.instance.tiles[tile.xPos / 8][tile.yPos / 8 - 1];
            case 3:
                return BG.instance.tiles[tile.xPos / 8][tile.yPos / 8 + 1];
            default:
                System.err.println("Wrong dir into GetAdjacentTile");
                return null;
        }
    }

    int GetOppositeDir(int dir) {
        switch (dir) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
            default:
                System.err.println("Wrong parameter into GetOppositeDir");
                return 0;
        }
    }

    int GetNewCornerDir() {
        for (int i = 0; i < 4; i++) {
            if (curTile.canGoDir[i] && i != GetOppositeDir(curDir)) {
                return i;
            }
        }
        System.err.println("Something went wrong with GetNewCornerTile");
        return -1;
    }

    double GetDistance(double x0, double y0, double x1, double y1) {
        return Math.sqrt(Math.pow((x0 - y0), 2) + Math.pow((x1 - y1), 2));
    }

    void die() {
        dead = true;
        leaveHome = true;
        Game.GL.eatGhost(ghostType);
    }

    void paint(Graphics2D g2d) {
        g2d.setColor(color);

        if (showTarget && !Game.GL.isFrightened(this)) {
            IntCoord target = dead ? homePos : GetMyTargetCoord();
            g2d.drawRect(target.x, target.y, 8, 8);
            g2d.drawLine((int) x + 8, (int) y + 8, target.x + 4, target.y + 4);
        }

        if (dead) {
            g2d.drawImage(images[rotationIndex + 6][0], (int) x, (int) y, null);
        } else if (!Game.GL.isFrightened(this)) {
            if (System.currentTimeMillis() % 300 > 150 || Game.GL.pauseAnim) {
                g2d.drawImage(images[rotationIndex][0], (int) x, (int) y, null);
            } else {
                g2d.drawImage(images[rotationIndex][1], (int) x, (int) y, null);
            }
        } else {
            int index = 4;
            if (Game.GL.frightenedTimer < 1.8f) {
                if (Game.GL.frightenedTimer >= 0f && Game.GL.frightenedTimer < 0.2f) {
                    index = 5;
                } else if (Game.GL.frightenedTimer >= 0.4f && Game.GL.frightenedTimer < 0.6f) {
                    index = 5;
                } else if (Game.GL.frightenedTimer >= 0.8f && Game.GL.frightenedTimer < 1f) {
                    index = 5;
                } else if (Game.GL.frightenedTimer >= 1.2f && Game.GL.frightenedTimer < 1.4f) {
                    index = 5;
                } else if (Game.GL.frightenedTimer >= 1.6f && Game.GL.frightenedTimer < 1.8f) {
                    index = 5;
                }
            }

            if (System.currentTimeMillis() % 300 > 150 || Game.GL.pauseAnim) {
                g2d.drawImage(images[index][0], (int) x, (int) y, null);
            } else {
                g2d.drawImage(images[index][1], (int) x, (int) y, null);
            }
        }

    }
}
