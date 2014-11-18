package snow;

import java.awt.*;

public abstract class PanningSceneObject extends SceneObject {
    private static final double MINIMUM_SECONDS_FOR_FALL = 30;

    public PanningSceneObject(int widthPixels, int heightPixels, double x, double y, double z) {
        super(widthPixels, heightPixels, x, y, z);
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / MINIMUM_SECONDS_FOR_FALL / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;
        double width = sceneBounds != null ? (double) widthPixels / sceneBounds.width : 0;

        x += delta;
    }
}
