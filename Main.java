import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files; 
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.Random; 

import java.sql.Timestamp;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUploadPhotoRequest;

import org.apache.http.client.ClientProtocolException;

/**
 * Instagram bot to upload hex colors
 * @author  Saul Neri
 * @since   Sunday, 17 May 2020.
 */
public class Main {
    static class Window extends JFrame {
        private Canvas canvas;
        private Instagram4j insta;
        FileWriter logger;

        public Window(String username, String password, int start, int numberOfPosts) {
            setSize(602, 624);
            setDefaultCloseOperation(EXIT_ON_CLOSE);;
            setLayout(new GridLayout(1,1));
            initComponents();
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            setVisible(true);

            try {
                logger = new FileWriter("log.txt", true);

                try {
                    login(username, password);
                    postImages(start, numberOfPosts);
                } catch (ClientProtocolException e) {
                    System.out.println("Instagram request failed");
                    String error = "[" + new Timestamp(System.currentTimeMillis()) + "] -> ERROR: ";
                    error += e.getMessage() + "\n";
                    logger.write(error);
                } catch (InterruptedException e) {
                    System.out.println("Sleeper failed");
                    String error = "[" + new Timestamp(System.currentTimeMillis()) + "] -> ERROR: ";
                    error += e.getMessage() + "\n";
                    logger.write(error);
                } catch (IOException e) {
                    System.out.println("Logger failed");
                    String error = "[" + new Timestamp(System.currentTimeMillis()) + "] -> ERROR: ";
                    error += e.getMessage() + "\n";
                    logger.write(error);
                }

                logger.close();

            } catch (IOException e) {
                System.out.println("Failed to open the log file");
                e.printStackTrace();
            }

            System.exit(0);
        }

        private void initComponents() {
            canvas = new Canvas();
            add(canvas);
        }

        private void login(String username, String password) throws ClientProtocolException, IOException {
            insta = Instagram4j.builder().username(username).password(password).build();
            insta.setup();
            insta.login();
        }

        private void postImages(int start, int numberOfPosts) throws ClientProtocolException, InterruptedException, IOException {
            int sleepSeconds = 0;
            Random rand = new Random();
            for (int n = 0; n < numberOfPosts; n++) {
                float percentage = ((float)n / (float)(numberOfPosts- 1.0f)) * 100.0f;
                System.out.println("n: " + n + " --> " + percentage + "%");

                uploadImage(start + n);
                
                sleepSeconds = rand.nextInt(61) + 120;
                Thread.sleep(sleepSeconds*1000);
            }

            System.out.println("===== FINISHED =====");
        }

        public void uploadImage(int start) throws ClientProtocolException, IOException {
            ArrayList<Color> colors = colorsList(start);
            String caption = createCaptionImage(colors, start);

            canvas.setColors(colors);
            
            BufferedImage image = new BufferedImage(602, 604, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = image.createGraphics();
            canvas.print(graphics);
            graphics.dispose();
            
            canvas.repaint();

            String imgPath = "./images/" + start + ".png";
            File imageFile = new File(imgPath);
            ImageIO.write(image, "png", imageFile);
            
            insta.sendRequest(new InstagramUploadPhotoRequest(
                imageFile,
                caption
            ));
            
            // Files.deleteIfExists(Paths.get(imgPath)); 

            String log = "[" + new Timestamp(System.currentTimeMillis()) + "] -> IMG: " + start + "\n";
            log += caption + "\n\n";
            logger.write(log);
        }

        private ArrayList<Color> colorsList(int start) {
            ArrayList<Color> colors = new ArrayList<>();
            int step = 559240;

            for (int i = 0; i < 30; i++) {
                int currentColorDecimal = start + i * step;
                int r = (currentColorDecimal / 256 / 256) % 256;
                int g = (currentColorDecimal / 256) % 256;
                int b = currentColorDecimal % 256;

                colors.add(new Color(r, g, b));
            }

            return colors;
        }

        private String createCaptionImage(ArrayList<Color> colors, int start) {
            String caption = "Hex code colors (" + start + "): \n";
            int index = 0;
            for (int row = 0; row < 10; row++) {
                for (int column = 0; column < 3; column++) {
                    Color c = colors.get(index);
                    caption += hexCodeRGB(c.getRed(), c.getGreen(), c.getBlue());
                    caption += " ";

                    index++;
                }
                caption += "\n";
            }
            
            return caption;
        }

        private String hexCodeRGB(int r, int g, int b) {
            String hexColor = "#";
            hexColor += Integer.toHexString(r).length() > 1 ? 
                            Integer.toHexString(r) : "0"+Integer.toHexString(r);
            hexColor += Integer.toHexString(g).length() > 1 ? 
                            Integer.toHexString(g) : "0"+Integer.toHexString(g);
            hexColor += Integer.toHexString(b).length() > 1 ? 
                            Integer.toHexString(b) : "0"+Integer.toHexString(b);
            
            return hexColor;
        }
    }

    static class Canvas extends JPanel {
        private ArrayList<Color> colors = new ArrayList<>();
        private int x = 0;
        private int y = 0;
        int width = 600;
        int height = 600;

        public void setColors(ArrayList<Color> colors) {
            this.colors = colors;
        }
        
        /**
         * Main drawing method
         * @param g Grpahics object context
         */
        public void paintComponent(Graphics g) {
            // super.paintComponent(g);
            RoundRectangle2D rect;
            Graphics2D g2 = (Graphics2D) g;

            x = 0;
            y = 0;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 602, 624);

            for (Color c : this.colors) {
                g2.setColor(c);
                rect = new RoundRectangle2D.Float(x+2, y+2, width/3-2, height/10-2, 20, 20);
                g2.fill(rect);

                x = (x + width/3);
                if (x >= width) {
                    x = 0;
                    y = (y + height/10);
                    if (y >= height) {
                        y = 0;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        int start = Integer.parseInt(args[2]);
        int numberOfPosts = Integer.parseInt(args[3]);
        new Window(username, password, start, numberOfPosts);
    }
}