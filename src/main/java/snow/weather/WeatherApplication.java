package snow.weather;

import snow.Util;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class WeatherApplication implements Runnable {
    private final Rectangle[] startUpWindowBounds;

    private WeatherApplication(Rectangle... startUpWindowBounds) {
        this.startUpWindowBounds = startUpWindowBounds;
    }

    public static void main(String[] args) {
        final Rectangle[] configurations = Util.getWindowBounds(args);

        SwingUtilities.invokeLater(new WeatherApplication(configurations));
    }

    @Override
    public void run() {
        WindowController windowController = WindowController.getInstance();
        for (Rectangle arg : startUpWindowBounds) {
            windowController.showNewWindow(
                    arg.x,
                    arg.y,
                    arg.width,
                    arg.height
            );
        }
    }
}
