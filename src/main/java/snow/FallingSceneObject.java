package snow;

import java.awt.*;

public abstract class FallingSceneObject extends SceneObject {
    private static final long MINIMUM_SECONDS_FOR_FALL = 5;

    public FallingSceneObject(int requestedWidthPixels, int requestedHeightPixels, double z, Rectangle sceneBounds) {
        super(requestedWidthPixels,
                requestedHeightPixels,
                Math.random(), -((double) requestedHeightPixels / sceneBounds.height), z
        );
    }

    @Override
    public void update(long msSinceLastUpdate, Rectangle sceneBounds) {
        double changePerMillisecond = speed / MINIMUM_SECONDS_FOR_FALL / 1000;
        double delta = msSinceLastUpdate * changePerMillisecond;

        y += delta;
        double degreesPerMillisecond = speed * (360.0 / MINIMUM_SECONDS_FOR_FALL / 1000);
        rotation = (rotation + (degreesPerMillisecond * msSinceLastUpdate)) % 360;
    }
}
