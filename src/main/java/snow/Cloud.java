package snow;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Cloud extends SceneObject {
    private static final double MINIMUM_SECONDS_FOR_FALL = 10;
    private static final int BLUR_MARGIN = 25;
    private static final int CLOUD_CIRCLE_DIAMETER = 100;

    public Cloud(int requestedWidthPixels, int requestedHeightPixels, double blur, Rectangle sceneBounds) {
        super(requestedWidthPixels,
                requestedHeightPixels,
                blur,
                -((double) requestedWidthPixels / sceneBounds.width),
                Math.random()
        );
    }

    @Override
    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        BufferedImage img = createEmptyImage(requestedWidthPixels + BLUR_MARGIN * 2, requestedHeightPixels + BLUR_MARGIN * 5);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.blue);
        for (int x = 0; x < requestedWidthPixels - CLOUD_CIRCLE_DIAMETER; x += CLOUD_CIRCLE_DIAMETER / 2) {
            for (int y = 0; y < requestedHeightPixels - CLOUD_CIRCLE_DIAMETER; y += CLOUD_CIRCLE_DIAMETER / 2) {
                g.fill(new Ellipse2D.Double(x + BLUR_MARGIN, y + BLUR_MARGIN, CLOUD_CIRCLE_DIAMETER, CLOUD_CIRCLE_DIAMETER));
            }
        }
        return img;
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / MINIMUM_SECONDS_FOR_FALL / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;
        double width = sceneBounds != null ? (double) widthPixels / sceneBounds.width : 0;

        x += delta;
    }
}
