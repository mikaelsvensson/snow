package snow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeatherController {
    private static WeatherController instance;

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
    private final int snowflakeWidth = 100;
    private final int snowflakeHeight = 100;

    public void start() {
        stop();
        for (int i = 0; i < 100; i++) {
            SnowFlake snowFlake = new SnowFlake(snowflakeWidth, snowflakeHeight);
            snowFlake.y = -snowFlake.radius;// * 100;
            snowFlakes.add(snowFlake);
        }
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                long lastTime = System.currentTimeMillis();
                long secondsToFall = 5;
                while (!Thread.currentThread().isInterrupted()) {
                    long now = System.currentTimeMillis();
                    long delay = now - lastTime;
                    lastTime = now;
                    for (SnowFlake snowFlake : snowFlakes) {
                        double changePerMillisecond = snowFlake.speed / secondsToFall / 1000;
                        double delta = delay * changePerMillisecond;
                        if (snowFlake.y + delta > 1.0) {
                            snowFlake.reset();
                        } else {
                            snowFlake.y += delta;
                            double degreesPerMillisecond = snowFlake.speed * (360.0 / secondsToFall / 1000);
                            snowFlake.rotation = (snowFlake.rotation + (degreesPerMillisecond * delay)) % 360;
                        }
                    }
                    fireListeners();
                    Thread.yield();
                }
            }
        };
        thread = new Thread(runnable);
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
}
