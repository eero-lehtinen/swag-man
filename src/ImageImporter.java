
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageImporter {

    static Image[][] getGhostImages(Ghost.GhostType gt) {

        Image[][] images = new Image[10][2];
        int yMultip = 0;

        if (gt == Ghost.GhostType.Blinky) {
            yMultip = 0;
        } else if (gt == Ghost.GhostType.Pinky) {
            yMultip = 1;
        } else if (gt == Ghost.GhostType.Inky) {
            yMultip = 2;
        } else if (gt == Ghost.GhostType.Clyde) {
            yMultip = 3;
        }
        try {
            BufferedImage img = ImageIO.read(Game.class.getResource("ghostAtlas.png"));
            for (int i = 0; i < 10; i++) {
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i < 4) {
                        images[i][i2] = img.getSubimage(i * 32 + i2 * 16, yMultip * 16, 16, 16);
                    } else if (i < 6) {
                        images[i][i2] = img.getSubimage((i - 4) * 32 + i2 * 16, 64, 16, 16);
                    } else if (i2 == 0) {
                        images[i][i2] = img.getSubimage(64 + ((i - 6) * 16), 64, 16, 16);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return images;
    }

    static BufferedImage[] getPlayerImages() {
        BufferedImage[] images = new BufferedImage[3];

        try {
            BufferedImage img = ImageIO.read(Game.class.getResource("new_pacmanAtlas.png"));
            for (int i = 0; i < 3; i++) {
                images[i] = img.getSubimage(i * 15, 0, 15, 15);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return images;
    }

    static BufferedImage[] getPlayerDeathImages() {
        BufferedImage[] images = new BufferedImage[11];

        try {
            BufferedImage img = ImageIO.read(Game.class.getResource("pacmanDeathAtlas.png"));
            for (int i = 0; i < 11; i++) {
                images[i] = img.getSubimage((i * 15) + (i * 5), 0, 15, 15);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return images;
    }

    static Image[] getNumbers() {
        Image[] images = new Image[10];
        BufferedImage img = null;
        try {
            img = ImageIO.read(Game.class.getResource("numberAtlas.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        for (int i = 0; i < 10; i++) {
            images[i] = img.getSubimage(i * 8, 0, 8, 8);
        }
        return images;
    }

    private static Image[] characters;
    private static Image[] numbers;

    static BufferedImage getImageOfText(String text) {

        if (characters == null) {
            setCharacters();
        }

        if (numbers == null) {
            numbers = getNumbers();
        }

        BufferedImage image = new BufferedImage(text.length() * 8, 8, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();

//      System.out.print(text + ": ");
        for (int i = 0; i < text.length(); i++) {
            int charValue = (int) text.charAt(i);

//          System.out.print(charValue + " ");
            // Letters
            if (charValue >= 97 && charValue <= 125) {
                g2d.drawImage(characters[charValue - 97], i * 8, 0, null);
            }

            // Numbers
            if (charValue >= 48 && charValue <= 57) {
                g2d.drawImage(numbers[charValue - 48], i * 8, 0, null);
            }

            // Percent
            if (charValue == 37) {
                g2d.drawImage(characters[26], i * 8, 0, null);
            }
            // Spacebar
            if (charValue == 32) {
                g2d.drawImage(characters[27], i * 8, 0, null);
            }
            //g2d.drawImage(numberImages[num], (xTile + i - length) * 8, 8 , null);
        }

//		System.out.println();
        return image;
    }

    static void setCharacters() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(Game.class.getResource("textAtlas.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        characters = new Image[img.getWidth() / 8];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = img.getSubimage(i * 8, 0, 8, 8);
        }
    }

    static Image[] getGhostPointImages() {
        Image[] images = new Image[4];
        BufferedImage img = null;
        try {
            img = ImageIO.read(Game.class.getResource("ghostPointAtlas.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        for (int i = 0; i < 4; i++) {
            images[i] = img.getSubimage(0, i * 8, 16, 8);
        }
        return images;
    }

    static Image[] getFruitPointImages() {
        Image[] images = new Image[8];
        BufferedImage img = null;
        try {
            img = ImageIO.read(Game.class.getResource("fruitPointAtlas.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        for (int i = 0; i < 8; i++) {
            images[i] = img.getSubimage(0, i * 8, 24, 8);
        }
        return images;
    }

    static Image[] getFruitImages() {
        Image[] images = new Image[8];
        BufferedImage img = null;
        try {
            img = ImageIO.read(Game.class.getResource("fruitAtlas.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        for (int i = 0; i < 8; i++) {
            images[i] = img.getSubimage(0, i * 16, 16, 16);
        }
        return images;
    }

    static Image getOneImage(String fileName) {
        Image image = null;
        try {
            image = ImageIO.read(Game.class.getResource(fileName));
        } catch (IOException e) {
            System.err.println(e);
        }
        return image;
    }

    static Image getIcon() {
        Image image = null;
        try {
            BufferedImage img = ImageIO.read(Game.class.getResource("ghostAtlas.png"));
            image = img.getSubimage(32, 0, 16, 16);
            image = image.getScaledInstance(64, 64, Image.SCALE_FAST);
        } catch (IOException e) {
            System.err.println(e);
        }
        return image;
    }
}
