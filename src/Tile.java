
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

public class Tile {

    public int xPos;
    public int yPos;

    public int tileType;
    public Color color;
    private BG bg;
    boolean[] canGoDir;
    Image energizer;
    public boolean notOnBoard;

    Tile(int x, int y, int _tileType, BG _bg) {

        xPos = x * 8;
        yPos = y * 8;
        tileType = _tileType;
        bg = _bg;
        if (_tileType == 0) {
            color = Color.red;
        } else if (_tileType == 1 || _tileType == 6) {
            color = Color.lightGray;
        } else if (_tileType == 2 || _tileType == 7) {
            color = Color.green;
        } else if (_tileType == 3 || _tileType == 8) {
            color = Color.yellow;
        } else if (_tileType == 4 || _tileType == 9) {
            color = Color.cyan;
        } else if (_tileType == 5) {
            color = Color.magenta;
            canGoDir = new boolean[4];
            canGoDir[0] = true;
            canGoDir[1] = true;
            canGoDir[2] = false;
            canGoDir[3] = false;
        } else if (_tileType == 10) {
            color = Color.lightGray;
            energizer = ImageImporter.getOneImage("energizer.png");
        } else if (_tileType == 11) {
            color = Color.green;
            energizer = ImageImporter.getOneImage("energizer.png");
        }
    }

    // Is not on board
    Tile(int x, int y) {
        xPos = x * 8;
        yPos = y * 8;
        notOnBoard = true;
    }

    public void FindFriendTiles() {
        canGoDir = new boolean[4];

        for (int dir = 0; dir < 4; dir++) {

            int x = xPos / 8;
            int y = yPos / 8;

            if (dir == 0) {
                x--;
            } else if (dir == 1) {
                x++;
            } else if (dir == 2) {
                y--;
            } else if (dir == 3) {
                y++;
            }

            if (bg.tiles[x][y].tileType != 0) {
                canGoDir[dir] = true;
            } else {
                canGoDir[dir] = false;
            }
        }
    }
    boolean firstTime = true;

    public void paint(Graphics2D g) {
        if (tileType > 0 && tileType < 5) {
            g.setColor(new Color(1f, 0.694117f, 0.552941f));
            g.fillRect(xPos + 3, yPos + 3, 2, 2);
        } else if (notOnBoard) {
            g.setColor(Color.black);
            g.fillRect(xPos, yPos, 8, 8);
        } else if (tileType == 10 || tileType == 11) {
            if (System.currentTimeMillis() % 300 > 150 || Game.GL.pauseAnim) {
                g.drawImage(energizer, xPos, yPos, null);
            }
        }

        /*g.setColor(color);
		g.drawRect(xPos + 1, yPos + 1, 6, 6);
		/*g.setColor(Color.magenta);
		if (friendTiles != null) {
			for (int i = 0; i < 4; i++) { 
				if (friendTiles[i] != this) {
					g.drawLine(xPos + 4, yPos + 4, friendTiles[i].xPos + 4, friendTiles[i].yPos + 4);
				}
			}
		}*/
    }
}
