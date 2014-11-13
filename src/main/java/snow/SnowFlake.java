package snow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SnowFlake extends FallingSceneObject {

    public SnowFlake(int widthPixels, int heightPixels, double z, Rectangle sceneBounds) {
        super(widthPixels, heightPixels, z, sceneBounds);
    }

    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        BufferedImage img = createEmptyImage(requestedWidthPixels, requestedHeightPixels);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double l1 = 0.2 + Math.random() * 0.8;
        double l2 = 0.5 + Math.random() * 0.5;
        double l3 = 0.5 + Math.random() * 0.5;
        double[] lengths = {l1, l1, l1, l1, l1, l1};
        int thickness = (int) (1 + 20 * Math.random());
//        int brightness = (int) ((0.7 + Math.random() * 0.3) * 255);
//        Color color = new Color(brightness, brightness, brightness);
        Color color = new Color(255, 255, 255);
//        Color color = new Color(25 * (int) (Math.random() * 10), 25 * (int) (Math.random() * 10), 25 * (int) (Math.random() * 10));
        g.setColor(color);
        for (int i = 0; i < 6; i++) {
            g.setTransform(AffineTransform.getRotateInstance(i * 2 * Math.PI / 6, requestedWidthPixels / 2, requestedHeightPixels / 2));
            int length = (int) (lengths[i] * requestedHeightPixels / 2);
            g.fillRect(requestedWidthPixels / 2 - (thickness / 2), requestedHeightPixels / 2 - length, thickness, length);
        }
        return img;
    }

}
