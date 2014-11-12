package snow;

import com.jhlabs.image.GaussianFilter;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public abstract class SceneObject {
    public BufferedImage image;
    public double rotation;
    double x;
    double y;
    double radius;
    double speed;
    int widthPixels;
    int heightPixels;

    void reset(double y) {
        radius = Math.random();
        this.x = Math.random();
        this.y = y;
        speed = 0.5 + 0.5 * Math.random();
    }

    public SceneObject(int widthPixels, int heightPixels, double blur) {
        reset(0);
        this.image = createImage(blur, widthPixels, heightPixels);
        this.widthPixels = image.getWidth();
        this.heightPixels = image.getHeight();
    }

    protected abstract BufferedImage createImage(double blur, int requestedWidthPixels, int requestedHeightPixels);

    /**
     * Filters from http://www.jhlabs.com/ip/index.html
     */
    protected BufferedImage createBlurredImage(BufferedImage img, double blur) {
        BufferedImageOp op;
//        op = new BoxBlurFilter(5, 5, 3);
        op = new GaussianFilter((float) (blur * 25));
        return op.filter(img, null);
    }
}
