package snow.weather;

import snow.Util;

import java.awt.*;

public abstract class FallingSceneObject extends SceneObject {
    private double rotationDirection = Util.random(0.3, 1.0) * (Util.random(0, 1) > 0.5 ? 1 : -1);
    private static int minimumSecondsForFall = 5;

    public FallingSceneObject(int requestedWidthPixels, int requestedHeightPixels, double z, Rectangle sceneBounds) {
        super(requestedWidthPixels,
                requestedHeightPixels,
                Math.random(), -((double) requestedHeightPixels / sceneBounds.height), z
        );
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / minimumSecondsForFall / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;

        y += delta;
        double degreesPerMillisecond = rotationDirection * (360.0 / minimumSecondsForFall / 1000);
        rotation = (rotation + (degreesPerMillisecond * msSinceLastUpdate)) % 360;
    }

    public static void changeSlowness(int delta) {
        minimumSecondsForFall = Math.max(1, Math.min(20, minimumSecondsForFall + delta));
    }

    public static int getSlowness() {
        return minimumSecondsForFall;
    }
}
