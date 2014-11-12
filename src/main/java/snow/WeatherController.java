package snow;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeatherController {
    private static final int SNOW_FLAKE_COUNT = 100;
    private static WeatherController instance;
    private static final long MINIMUM_SECONDS_FOR_FALL = 5;
    private Rectangle sceneBounds;

    public static interface Listener {
        void onSnowFlakeChange();
    }

    private WeatherController() {
    }

    private Collection<Listener> listeners = new ArrayList<>();

    public synchronized void addListener(Listener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private List<SceneObject> sceneObjects = new ArrayList<>();
    private Thread thread;
    private final int snowflakeWidth = 200;
    private final int snowflakeHeight = 200;

    public void start() {
        stop();
        for (int i = 0; i < SNOW_FLAKE_COUNT; i++) {
            double blur = 1.0 - (((double) i / SNOW_FLAKE_COUNT) * 10) / 10;
            double v = Math.random();
            if (v < 0.1) {
                sceneObjects.add(new DayBubble(100, 100, blur));
            } else {
                sceneObjects.add(new SnowFlake(snowflakeWidth, snowflakeHeight, blur));
            }
        }
        thread = new Thread(new SnowFlakeMoverRunnable());
        thread.start();
    }

    private synchronized void fireListeners() {
        for (Listener listener : listeners) {
            listener.onSnowFlakeChange();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public List<SceneObject> getSnowFlakes() {
        return sceneObjects;
    }

    public void setSceneBounds(Rectangle sceneBounds) {
        this.sceneBounds = sceneBounds;
    }

    public static WeatherController getInstance() {
        if (instance == null) {
            synchronized (WeatherController.class) {
                if (instance == null) {
                    instance = new WeatherController();
                }
            }
        }
        return instance;
    }

    private class SnowFlakeMoverRunnable implements Runnable {

        @Override
        public void run() {
            long lastTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.currentTimeMillis();
                long delay = now - lastTime;
                lastTime = now;
                for (SceneObject sceneObject : sceneObjects) {
                    double changePerMillisecond = sceneObject.speed / MINIMUM_SECONDS_FOR_FALL / 1000;
                    double delta = delay * changePerMillisecond;

                    double height = sceneBounds != null ? (double) sceneObject.heightPixels / sceneBounds.height : 0;

                    if (sceneObject.y - height > 1.0) {
                        sceneObject.reset(-height);
                    } else {
                        sceneObject.y += delta;
                        double degreesPerMillisecond = sceneObject.speed * (360.0 / MINIMUM_SECONDS_FOR_FALL / 1000);
                        sceneObject.rotation = (sceneObject.rotation + (degreesPerMillisecond * delay)) % 360;
                    }
                }
                fireListeners();
                Thread.yield();
            }
        }
    }
}
