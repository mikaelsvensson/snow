package snow;

import com.jhlabs.image.GaussianFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public abstract class SceneObject {
    public BufferedImage image;
    public double rotation;
    double x;
    double y;
    double speed;
    int widthPixels;
    int heightPixels;

    public SceneObject(int widthPixels, int heightPixels, double blur, double x1, double y1) {
        this.x = x1;
        this.y = y1;
        speed = 0.5 + 0.5 * Math.random();
        init(widthPixels, heightPixels, blur);
    }

    protected abstract BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels);

    public abstract void update(long msSinceLastUpdate, Rectangle sceneBounds);

    public void init(int widthPixels, int heightPixels, double blur) {
        this.image = createBlurredImage(createImage(widthPixels, heightPixels), blur);
        this.widthPixels = image.getWidth();
        this.heightPixels = image.getHeight();
    }

    /**
     * Filters from http://www.jhlabs.com/ip/index.html
     */
    protected BufferedImage createBlurredImage(BufferedImage img, double blur) {
        BufferedImageOp op;
//        op = new BoxBlurFilter(5, 5, 3);
        op = new GaussianFilter((float) (blur * 25));
        return op.filter(img, null);
    }

    protected BufferedImage createEmptyImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    }
}
