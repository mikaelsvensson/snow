package snow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SnowFlake {
    double x;
    double y;
    double radius;
    double speed;
    public BufferedImage image;
    public double rotation;
    int width;
    int height;

    public SnowFlake(int width, int height) {
        reset();
        this.width = width;
        this.height = height;
        image = createImage();
    }

    void reset() {
        x = Math.random();
        radius = Math.random();
        y = -radius;
        speed = 0.5 + 0.5 * Math.random();
    }

    private BufferedImage createImage() {
        BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double l1 = 0.5 + Math.random() * 0.5;
        double l2 = 0.5 + Math.random() * 0.5;
        double l3 = 0.5 + Math.random() * 0.5;
        double[] lengths = {l1, l1, l1, l1, l1, l1};
        int thickness = (int) (1 + 20 * Math.random());
//        int brightness = (int) ((0.7 + Math.random() * 0.3) * 255);
//        Color color = new Color(brightness, brightness, brightness);
        Color color = new Color(
                25 * (int)(Math.random() * 10),
                25 * (int)(Math.random() * 10),
                25 * (int)(Math.random() * 10)
        );
        g.setColor(color);
        for (int i = 0; i < 6; i++) {
            g.setTransform(AffineTransform.getRotateInstance(i * 2 * Math.PI / 6, this.width / 2, this.height / 2));
            int length = (int) (lengths[i] * this.height / 2);
            g.fillRect(this.width / 2 - (thickness / 2), this.height / 2 - length, thickness, length);
        }
        return img;
    }
}
