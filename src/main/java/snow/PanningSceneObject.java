package snow;

import java.awt.*;

public abstract class PanningSceneObject extends SceneObject {
    private static int minimumSecondsForFall = 30;

    public PanningSceneObject(int widthPixels, int heightPixels, double x, double y, double z) {
        super(widthPixels, heightPixels, x, y, z);
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / minimumSecondsForFall / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;
        double width = sceneBounds != null ? (double) widthPixels / sceneBounds.width : 0;

        x += delta;
    }

    public static void changeSlowness(int delta) {
        minimumSecondsForFall = Math.max(1, Math.min(60, minimumSecondsForFall + delta));
    }

    public static int getSlowness() {
        return minimumSecondsForFall;
    }
}
