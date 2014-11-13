package snow;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WeatherController {
    private static final int SNOW_FLAKE_COUNT = 100;
    private static WeatherController instance;
    private Rectangle sceneBounds;
    private Thread cleanUpThread;

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
    private SceneObject[] sceneObjectsCopy = null;
    private Thread thread;
    private final int snowflakeWidth = 200;
    private final int snowflakeHeight = 200;

    public void start() {
        stop();
        synchronized (this) {
            for (int i = 0; i < SNOW_FLAKE_COUNT; i++) {
                double blur = 1.0 - (((double) i / SNOW_FLAKE_COUNT) * 10) / 10;
                sceneObjects.add(createRandomSceneObject(blur));
            }
            updateSceneObjectsCopy();
        }
        thread = new Thread(new SnowFlakeMoverRunnable());
        thread.start();
        cleanUpThread = new Thread(new CleanUpRunnable());
        cleanUpThread.setPriority(Thread.MIN_PRIORITY);
        cleanUpThread.start();
    }

    private SceneObject createRandomSceneObject(double blur) {
        double v = Math.random();
        SceneObject obj;
        if (v < 0.1) {
            obj = new DayBubble(100, 100, blur, sceneBounds);
        } else if (v < 0.15) {
            obj = new Cloud(300, 300, blur, sceneBounds);
        } else {
            obj = new SnowFlake(snowflakeWidth, snowflakeHeight, blur, sceneBounds);
        }
        return obj;
    }

    private synchronized void fireListeners() {
        for (Listener listener : listeners) {
            listener.onSnowFlakeChange();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (cleanUpThread != null) {
            cleanUpThread.interrupt();
            thread = null;
        }
    }

    public SceneObject[] getSnowFlakes() {
        synchronized (this) {
            return sceneObjectsCopy;
        }
    }

    public void setSceneBounds(Rectangle sceneBounds) {
        this.sceneBounds = sceneBounds;
        if (thread == null) {
            start();
        }
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
                synchronized (WeatherController.this) {
                    for (SceneObject sceneObject : sceneObjectsCopy) {
                        sceneObject.update(delay, sceneBounds);
                    }
                }
                fireListeners();
                Thread.yield();
            }
        }
    }

    private void updateSceneObjectsCopy() {
        synchronized (this) {
            sceneObjectsCopy = sceneObjects.toArray(new SceneObject[sceneObjects.size()]);
        }
    }

    private class CleanUpRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (sceneObjects) {
                    Iterator<SceneObject> it = sceneObjects.iterator();
                    int i = 0;
                    List<Double> newObjectsBlurs = new ArrayList<>();
                    while (it.hasNext()) {
                        SceneObject next = it.next();

                        double width = sceneBounds != null ? (double) next.widthPixels / sceneBounds.width : 0;
                        double height = sceneBounds != null ? (double) next.heightPixels / sceneBounds.height : 0;

                        boolean isObjectOutsideScene = next.x - width/2 > 1.0 || next.y - height/2 > 1.0;
                        if (isObjectOutsideScene) {
                            it.remove();
                            double blur = 1.0 - (((double) i / SNOW_FLAKE_COUNT) * 10) / 10;
                            newObjectsBlurs.add(blur);
                        }
                        i++;
                    }
                    for (Double blur : newObjectsBlurs) {
                        sceneObjects.add(createRandomSceneObject(blur));
                        Thread.yield();
                    }
                    updateSceneObjectsCopy();
                }
            }
        }
    }
}
