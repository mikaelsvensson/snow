package snow;

import java.awt.*;
import java.awt.image.BufferedImage;

class PhotoSceneObject extends PanningSceneObject {
    private final BufferedImage bufferedImage;

    public PhotoSceneObject(BufferedImage bufferedImage, Rectangle sceneBounds) {
        super(bufferedImage.getWidth(), bufferedImage.getHeight(), -((double) bufferedImage.getWidth() / sceneBounds.width), Math.random(), Util.random(0.0, 0.1));
        this.bufferedImage = bufferedImage;
    }

    @Override
    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        return bufferedImage;
    }
}
