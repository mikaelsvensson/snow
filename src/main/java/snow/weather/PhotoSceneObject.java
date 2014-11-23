package snow.weather;

import snow.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

class PhotoSceneObject extends PanningSceneObject {
    private final BufferedImage bufferedImage;

    public PhotoSceneObject(BufferedImage bufferedImage, Rectangle sceneBounds) {
        super(bufferedImage.getWidth(), bufferedImage.getHeight(), -((double) bufferedImage.getWidth() / sceneBounds.width), Math.random(), Util.random(0.0, 0.1));
        this.bufferedImage = bufferedImage;
        super.init(bufferedImage.getWidth(), bufferedImage.getHeight(), z);
    }

    @Override
    public void init(int widthPixels, int heightPixels, double z) {
        // Do nothing, otherwise things will get ugly since this method is called by the super-class constructor before the bufferedImage field is set. Invoke the super-class method later, when the bufferedImage field HAS been set.
    }

    @Override
    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        return bufferedImage;
    }
}
