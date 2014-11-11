package snow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeatherController {
    private static final int SNOW_FLAKE_COUNT = 100;
    private static WeatherController instance;
    private static final long MINIMUM_SECONDS_FOR_FALL = 5;

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

    private List<SnowFlake> snowFlakes = new ArrayList<>();
    private Thread thread;
    private double windyness = StrictMath.toRadians(50.0);
    private final int snowflakeWidth = 200;
    private final int snowflakeHeight = 200;

    public void start() {
        stop();
        for (int i = 0; i < SNOW_FLAKE_COUNT; i++) {
            snowFlakes.add(new SnowFlake(snowflakeWidth, snowflakeHeight, 1.0 - (((double)i / SNOW_FLAKE_COUNT)*10)/10));
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

    public List<SnowFlake> getSnowFlakes() {
        return snowFlakes;
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
                for (SnowFlake snowFlake : snowFlakes) {
                    double changePerMillisecond = snowFlake.speed / MINIMUM_SECONDS_FOR_FALL / 1000;
                    double delta = delay * changePerMillisecond;
                    if (snowFlake.y + delta > 1.0) {
                        snowFlake.reset();
                    } else {
                        snowFlake.y += delta;
                        double degreesPerMillisecond = snowFlake.speed * (360.0 / MINIMUM_SECONDS_FOR_FALL / 1000);
                        snowFlake.rotation = (snowFlake.rotation + (degreesPerMillisecond * delay)) % 360;
                    }
                }
                fireListeners();
                Thread.yield();
            }
        }
    }
}
