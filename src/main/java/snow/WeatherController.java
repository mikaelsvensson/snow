package snow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WeatherController {
    private int objectCount = 50;
    private static WeatherController instance;
    private Rectangle sceneBounds;
    private Thread cleanUpThread;
    private Thread imageFolderMonitorThread;

    public void changeFallingObjectSlowness(int delta) {
        synchronized (sceneObjects) {
            FallingSceneObject.changeSlowness(delta);
        }
    }

    public static interface Listener {
        void onSceneChange();
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

    public void changeObjectCount(int delta) {
        objectCount = Math.max(0, objectCount + delta);
    }

    private List<SceneObject> sceneObjects = new ArrayList<>();
    private SceneObject[] sceneObjectsCopy = null;
    private Thread thread;
    private final int snowflakeWidth = 200;
    private final int snowflakeHeight = 200;

    public void start() {
        stop();
        addMissingSceneObjects();
        thread = new Thread(new ObjectUpdaterRunnable());
        thread.start();
        cleanUpThread = new Thread(new CleanUpRunnable());
        cleanUpThread.setPriority(Thread.MIN_PRIORITY);
        cleanUpThread.start();
        imageFolderMonitorThread = new Thread(new ImageFolderMonitorRunnable());
        imageFolderMonitorThread.setPriority(Thread.MIN_PRIORITY);
        imageFolderMonitorThread.start();
    }

    private SceneObject createRandomSceneObject(double z) {
        double v = Math.random();
        SceneObject obj;
        if (v < 0.1) {
            obj = new DayBubble(100, 100, z, sceneBounds);
//        } else if (v < 0.15) {
//            obj = new Cloud(300, 300, z, sceneBounds);
        } else {
            obj = new SnowFlake(snowflakeWidth, snowflakeHeight, z, sceneBounds);
        }
        return obj;
    }

    private synchronized void fireListeners() {
        for (Listener listener : listeners) {
            listener.onSceneChange();
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
        if (imageFolderMonitorThread != null) {
            imageFolderMonitorThread.interrupt();
            thread = null;
        }
    }

    public SceneObject[] getSceneObjects() {
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

    private class ObjectUpdaterRunnable implements Runnable {

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
                    while (it.hasNext()) {
                        SceneObject next = it.next();

                        double width = sceneBounds != null ? (double) next.widthPixels / sceneBounds.width : 0;
                        double height = sceneBounds != null ? (double) next.heightPixels / sceneBounds.height : 0;

                        boolean isObjectOutsideScene = next.x - width / 2 > 1.0 || next.y - height / 2 > 1.0;
                        if (isObjectOutsideScene) {
                            it.remove();
                        }
                    }
                    addMissingSceneObjects();
                }
            }
        }
    }

    private class ImageFolderMonitorRunnable implements Runnable {

        private List<File> filesThen = null;

        @Override
        public void run() {
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            while (!Thread.currentThread().isInterrupted()) {
                List<File> filesNow = Arrays.asList(folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpg");
                    }
                }));

                if (filesThen != null) {
                    for (final File file : filesNow) {
                        if (!filesThen.contains(file)) {
                            // New file
                            try {
                                final BufferedImage bufferedImage = ImageIO.read(file);
                                synchronized (sceneObjects) {
                                    sceneObjects.add(new PhotoSceneObject(bufferedImage, sceneBounds));
                                    sortSceneObjectByZ();
                                }
                                updateSceneObjectsCopy();
                                Thread.yield();
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            break; // Only load the first new image each second
                        }
                    }
                }

                filesThen = filesNow;

                try {
                    Thread.sleep(1000); // Sleep for a second
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void addMissingSceneObjects() {
        synchronized (sceneObjects) {
            while (sceneObjects.size() < objectCount) {
                sceneObjects.add(createRandomSceneObject(Math.random()));
//                        Thread.yield();
            }
            sortSceneObjectByZ();
        }
        updateSceneObjectsCopy();
    }

    private void sortSceneObjectByZ() {
        Collections.sort(sceneObjects, new Comparator<SceneObject>() {
            @Override
            public int compare(SceneObject o1, SceneObject o2) {
                return (int) (o1.z - o2.z);
            }
        });
    }

}
