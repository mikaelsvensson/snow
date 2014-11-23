package snow.weather;

import java.awt.*;

public abstract class PanningSceneObject extends SceneObject {
    private static int minimumSecondsForFall = 30;

    PanningSceneObject(int widthPixels, int heightPixels, double x, double y, double z) {
        super(widthPixels, heightPixels, x, y, z);
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / minimumSecondsForFall / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;

        x += delta;
    }

    public static void changeSlowness(int delta) {
        minimumSecondsForFall = Math.max(1, Math.min(60, minimumSecondsForFall + delta));
    }

    public static int getSlowness() {
        return minimumSecondsForFall;
    }
}
