
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Menu extends JPanel implements KeyListener {

    Menu() {
        addKeyListener(this);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                requestFocusInWindow();
            }
        }, 20);
    }

    int pointerPos;
    int pointerValueMin = 0;
    int pointerValueMax = 2;
    boolean inOptions;
    boolean itemChosen;
    boolean leftChosen;
    boolean rightChosen;

    int[][] triaglePointsX = {{90, 80, 90}, {134, 144, 134}};
    int[] triaglePointsY = {140, 146, 152};

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(Main.scaleFactor, Main.scaleFactor);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 224, 288);

        if (inOptions) {
            g2d.drawImage(getColoredVersion(ImageImporter.getImageOfText("options"), new Color(80, 80, 255, 255)).getScaledInstance(112, 16, Image.SCALE_FAST), 112 - 56, 60 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText("window scale factor"), 112 - (19 * 8 / 2), 130 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText(String.valueOf(Main.scaleFactor)), 112 - (String.valueOf(Main.scaleFactor).length() * 8 / 2), 150 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText("back"), 112 - (4 * 8 / 2), 180 - 8, null);
            g2d.drawImage(getColoredVersion(ImageImporter.getImageOfText("f to show ghost targets"), new Color(120, 120, 120, 255)), 112 - ("f to show ghost targets".length() * 8 / 2), 240, null);
        } else {
            g2d.drawImage(getColoredVersion(ImageImporter.getImageOfText("swag man"), new Color(80, 80, 255, 255)).getScaledInstance(128, 16, Image.SCALE_FAST), 112 - 64, 60 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText("start"), 112 - (5 * 8 / 2), 120 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText("options"), 112 - (7 * 8 / 2), 150 - 8, null);
            g2d.drawImage(ImageImporter.getImageOfText("exit"), 112 - (4 * 8 / 2), 180 - 8, null);
            g2d.drawImage(getColoredVersion(ImageImporter.getImageOfText("plus and minus keys"), new Color(120, 120, 120, 255)), 112 - ("plus and minus keys".length() * 8 / 2), 240, null);
            g2d.drawImage(getColoredVersion(ImageImporter.getImageOfText("to change volume"), new Color(120, 120, 120, 255)), 112 - ("to change volume".length() * 8 / 2), 250, null);
        }

        //g.setColor(new Color(1f, 0.694117f, 0.552941f));
        g.setColor(Color.WHITE);
        if (inOptions && pointerPos == 1) {
            g.drawRect(97, 106 + (pointerPos * 30), 30, 21);

            if (leftChosen) {
                g.fillPolygon(triaglePointsX[0], triaglePointsY, 3);
            } else {
                g.drawPolygon(triaglePointsX[0], triaglePointsY, 3);
            }

            if (rightChosen) {
                g.fillPolygon(triaglePointsX[1], triaglePointsY, 3);
            } else {
                g.drawPolygon(triaglePointsX[1], triaglePointsY, 3);
            }
        } else {
            g.drawRect(62, 106 + (pointerPos * 30), 100, 21);
        }

        g2d.drawImage(ImageImporter.getImageOfText("made by eero lehtinen"), 112 - 86, 279, null);

    }

    void gotoOptions() {
        inOptions = true;
        pointerPos = 1;
        pointerValueMin = 1;
        pointerValueMax = 2;
    }

    void gotoMainMenu() {
        inOptions = false;
        pointerPos = 0;
        pointerValueMin = 0;
        pointerValueMax = 2;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            if (pointerPos > pointerValueMin) {
                pointerPos--;
                Main.audioManager.playWaka();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            if (pointerPos < pointerValueMax) {
                pointerPos++;
                Main.audioManager.playWaka();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            if (inOptions && pointerPos == 1 && Main.scaleFactor > 1) {
                Main.scaleFactor--;
                Main.frame.getContentPane().setPreferredSize(new Dimension(224 * Main.scaleFactor, 288 * Main.scaleFactor));
                Main.frame.pack();
                leftChosen = true;
                Main.audioManager.playWaka();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            if (inOptions && pointerPos == 1 && Main.scaleFactor < 20) {
                Main.scaleFactor++;
                Main.frame.getContentPane().setPreferredSize(new Dimension(224 * Main.scaleFactor, 288 * Main.scaleFactor));
                Main.frame.pack();
                rightChosen = true;
                Main.audioManager.playWaka();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!inOptions) {
                if (pointerPos == 0) {
                    Main.openGame();
                } else if (pointerPos == 1) {
                    gotoOptions();
                } else if (pointerPos == 2) {
                    System.exit(0);
                }

                Main.audioManager.eatFruit.play();
            } else {
                if (pointerPos == 2) {
                    gotoMainMenu();
                    Main.audioManager.eatFruit.play();
                }
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (inOptions) {
                gotoMainMenu();
                Main.audioManager.eatFruit.play();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
            Main.audioManager.SetLinearMasterVolume(Main.audioManager.linearMasterVolume + 0.05f, true);
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            Main.audioManager.SetLinearMasterVolume(Main.audioManager.linearMasterVolume - 0.05f, true);
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            leftChosen = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            rightChosen = false;
        }

        repaint();
    }

    public Image getColoredVersion(BufferedImage image, Color color) {

        int height = image.getHeight();
        int width = image.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);

                int b = (p >> 0) & 0xff;

                // if blue channel is 0, set this pixel to set color
                if (b > 0) {
                    p = (255 << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
                }

                image.setRGB(x, y, p);
            }
        }

        return image;
    }
}
