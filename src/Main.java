
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Main {

    static Menu menu;
    static Game game;
    static JFrame frame;
    static AudioManager audioManager;

    public static int scaleFactor = 3;

    public static void main(String[] args) {

        scaleFactor = (int) Math.floor(Toolkit.getDefaultToolkit().getScreenSize().height / 310f);

        frame = new JFrame("Swag-man");
        frame.setIconImage(ImageImporter.getIcon());
        frame.getContentPane().setPreferredSize(new Dimension(224 * scaleFactor, 288 * scaleFactor));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        audioManager = new AudioManager();

        openMenu();
    }

    static void openGame() {
        new Timer().schedule(new OpenGameTask(), 200);
    }

    static class OpenGameTask extends TimerTask {

        @Override
        public void run() {
            if (menu != null) {
                frame.remove(menu);
            }
            menu = null;
            game = new Game();
            frame.add(game);
            frame.pack();
        }
    };

    static void openMenu() {
        if (game != null) {
            frame.remove(game);
        }
        game = null;
        menu = new Menu();
        frame.add(menu);
        frame.pack();
    }

}
