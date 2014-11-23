package snow.weather;

import com.jhlabs.image.GaussianFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public abstract class SceneObject {
    public BufferedImage image;
    public double rotation;
    double x;
    double y;
    final double z;
    final double speed;
    int widthPixels;
    int heightPixels;

    /**
     * @param widthPixels
     * @param heightPixels
     * @param x
     * @param y            The distance from point-of-view (the user) to the object. 0 = right in the front, 1 = as far away as possible.
     * @param z
     */
    SceneObject(int widthPixels, int heightPixels, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        speed = 0.5 + 0.5 * (1.0 - z);
        init(widthPixels, heightPixels, z);
    }

    protected abstract BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels);

    public abstract void update(long msSinceLastUpdate, Rectangle sceneBounds);

    /**
     * @param widthPixels
     * @param heightPixels
     * @param z
     */
    void init(int widthPixels, int heightPixels, double z) {
        this.image = createBlurredImage(createImage((int) (widthPixels * (1.0 - Math.min(z, 0.1))), (int) (heightPixels * (1.0 - Math.min(z, 0.1)))), z);
        this.widthPixels = image.getWidth();
        this.heightPixels = image.getHeight();
    }

    /**
     * Filters from http://www.jhlabs.com/ip/index.html
     */
    BufferedImage createBlurredImage(BufferedImage img, double blur) {
        BufferedImageOp op;
//        op = new BoxBlurFilter(5, 5, 3);
        op = new GaussianFilter((float) (blur * 25));
        return op.filter(img, null);
    }

    BufferedImage createEmptyImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    }
}
