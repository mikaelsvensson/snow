package snow.weather;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class WeatherApplication {
    public static void main(String[] args) {
        final String[] configurations;
        if (args.length == 1 && "fullscreen".equals(args[0])) {
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            configurations = new String[screenDevices.length];
            for (int i = 0; i < screenDevices.length; i++) {
                GraphicsDevice screenDevice = screenDevices[i];
                Rectangle bounds = screenDevice.getDefaultConfiguration().getBounds();
                configurations[i] = bounds.x + "," + bounds.y + "," + bounds.width + "," + bounds.height;
            }
        } else {
            configurations = args;
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                WindowController wc = WindowController.getInstance();
                for (String arg : configurations) {
                    String[] part = arg.split(",");
                    wc.showNewWindow(
                            Integer.parseInt(part[0]),
                            Integer.parseInt(part[1]),
                            Integer.parseInt(part[2]),
                            Integer.parseInt(part[3]),
                            part.length > 4 ? part[4] : null);
                }
            }

        });
    }

}
