package snow.weather;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class WeatherApplication implements Runnable {
    private Rectangle[] startUpWindowBounds;

    public WeatherApplication(Rectangle... startUpWindowBounds) {
        this.startUpWindowBounds = startUpWindowBounds;
    }

    public static void main(String[] args) {
        final Rectangle[] configurations;
        if (args.length == 1 && "fullscreen".equals(args[0])) {
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            configurations = new Rectangle[screenDevices.length];
            for (int i = 0; i < screenDevices.length; i++) {
                GraphicsDevice screenDevice = screenDevices[i];
                Rectangle bounds = screenDevice.getDefaultConfiguration().getBounds();
                configurations[i] = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        } else {
            configurations = new Rectangle[args.length];
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                String[] part = arg.split(",");
                configurations[i] = new Rectangle(
                        Integer.parseInt(part[0]),
                        Integer.parseInt(part[1]),
                        Integer.parseInt(part[2]),
                        Integer.parseInt(part[3]));
            }
        }

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
                    arg.height,
                    null);
        }
    }
}
