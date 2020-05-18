import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Files; 
import java.nio.file.Paths;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUploadPhotoRequest;

/**
 * Instagram bot to upload hex colors
 * @author  Saul Neri
 * @since   Sunday, 17 May 2020.
 */
public class Main {
    static class Window extends JFrame {
        private Canvas canvas;
        private Instagram4j insta;

        public Window(String username, String password, int start, int end) {
            setSize(400, 400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new GridLayout(1,1));
            initComponents();
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            setVisible(true);

            try {
                insta = Instagram4j.builder().username(username).password(password).build();
                insta.setup();
                insta.login();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            createImages(start, end);
        }

        public void initComponents() {
            canvas = new Canvas();
            add(canvas);
        }

        public void createImages(int start, int end) {
            BufferedImage image;
            Graphics graphics;

            int ri = (start / 256 / 256) % 256;
            int rf = (end / 256 / 256) % 256;
            int gi = (start / 256) % 256;
            int gf = (end / 256) % 256;
            int bi = start % 256;
            int bf = end % 256;

            for (int r = ri; r <= rf; r++) {
                for (int g = gi; g <= gf; g++) {
                    for (int b = bi; b <= bf; b++) {
                        canvas.setMainColor(r, g, b);
                        canvas.repaint();

                        String hexColor = "";
                        hexColor += Integer.toHexString(r).length() > 1 ? 
                                        Integer.toHexString(r) : "0"+Integer.toHexString(r);
                        hexColor += Integer.toHexString(g).length() > 1 ? 
                                        Integer.toHexString(g) : "0"+Integer.toHexString(g);;
                        hexColor += Integer.toHexString(b).length() > 1 ? 
                                        Integer.toHexString(b) : "0"+Integer.toHexString(b);;

                        System.out.println(r + " " + g + " " + b + " " + hexColor);
                        
                        image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                        graphics = image.createGraphics();
                        canvas.print(graphics);
                        graphics.dispose();
                        try {
                            File imageFile = new File("./images/" + hexColor + ".jpg");
                            ImageIO.write(image, "jpg", imageFile);
                            insta.sendRequest(new InstagramUploadPhotoRequest(
                                new File("./images/" + hexColor + ".jpg"),
                                "#" + hexColor + " #" + hexColor.toUpperCase()
                            ));
                            Files.deleteIfExists(Paths.get("./images/" + hexColor + ".jpg")); 
                        } catch (Exception e) { 
                            e.printStackTrace();
                        }
                    }
                }
            } 
        }
    }

    static class Canvas extends JPanel {
        private Color mainColor = new Color(0, 0, 0);

        public void setMainColor(int r, int g, int b) {
            mainColor = new Color(r, g, b);
        }

        /**
         * Main drawing method
         * @param g Grpahics object context
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            g.setColor(mainColor);
            g.fillRect(0, 0, 400, 400);	
        }
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        int start = Integer.parseInt(args[2]);
        int end = Integer.parseInt(args[3]);
        new Window(username, password, start, end);
    }
}