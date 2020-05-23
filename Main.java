import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Files; 
import java.nio.file.Paths;
import java.util.ArrayList;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUploadAlbumRequest;

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
            setSize(402, 424);
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
            
            createImages(start);
        }

        public void initComponents() {
            canvas = new Canvas();
            add(canvas);
            canvas.repaint();
        }

        private String hexCodeRGB(int r, int g, int b) {
            String hexColor = "";
            hexColor += Integer.toHexString(r).length() > 1 ? 
                            Integer.toHexString(r) : "0"+Integer.toHexString(r);
            hexColor += Integer.toHexString(g).length() > 1 ? 
                            Integer.toHexString(g) : "0"+Integer.toHexString(g);
            hexColor += Integer.toHexString(b).length() > 1 ? 
                            Integer.toHexString(b) : "0"+Integer.toHexString(b);
            
            System.out.println(r + " " + g + " " + b + " " + hexColor);
            return hexColor;
        }

        private String createCaptionImage(int ri, int g, int b) {
            String caption = "";
            caption += "PAGE " + (ri/32 + 1) + ":\n";
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    
                }
            }
            return caption;

        }

        public void createImages(int start) {
            BufferedImage image = new BufferedImage(402, 404, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = image.createGraphics();
            ArrayList<File> files = new ArrayList<>();
            String caption = "";

            int ri = (start / 256 / 256) % 256;
            // int rf = (end / 256 / 256) % 256;
            int gi = (start / 256) % 256;
            // int gf = (end / 256) % 256;
            int bi = start % 256;
            // int bf = end % 256;
            
            while (ri < 256) {
                canvas.setInitialColor(ri, gi, bi);
                
                image = new BufferedImage(402, 404, BufferedImage.TYPE_INT_RGB);
                graphics = image.createGraphics();
                canvas.print(graphics);
                try {
                    File imageFile = new File("./images/" + ri + ".jpg");
                    files.add(imageFile);
                    // caption += createCaptionImage(ri, gi, bi);
                    ImageIO.write(image, "jpg", imageFile);
                    ri += 32;
                } catch (Exception e) { 
                    e.printStackTrace();
                }
            }
            System.out.println(files.size());
            try {
                Object res = insta.sendRequest(new InstagramUploadAlbumRequest(
                    files,
                    "#test"
                ));

                System.out.println("------------> " + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Files.deleteIfExists(Paths.get("./images/" + hexColor + ".jpg")); 
        }
    }

    static class Canvas extends JPanel {
        private int red = 0;
        private int green = 0;
        private int blue = 0;
        private int x = 0;
        private int y = 0;
        int width = 400;
        int height = 400;

        public void setInitialColor(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;
        }
        
        /**
         * Main drawing method
         * @param g Grpahics object context
         */
        public void paintComponent(Graphics g) {
            // super.paintComponent(g);
            RoundRectangle2D rect;
            Graphics2D g2 = (Graphics2D) g;

            int runningRed = red;
            x = 0;
            y = 0;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 402, 424);

            for (int i = 0; i < 32; i++) {
                g2.setColor(new Color(runningRed, green, blue));
                rect = new RoundRectangle2D.Float(x+2, y+2, width/4-2, height/8-2, 15, 15);
                g2.fill(rect);

                x = (x + width/4);
                if (x >= width) {
                    x = 0;
                    y = (y + height/8);
                    if (y >= height) {
                        y = 0;
                    }
                }
                runningRed++;
            }
        }
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        int start = Integer.parseInt(args[2]);
        // int end = Integer.parseInt(args[3]);
        new Window(username, password, start, 0);
    }
}