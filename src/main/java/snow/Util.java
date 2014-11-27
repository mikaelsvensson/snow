package snow;

import java.awt.*;

public class Util {
    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public static int getServerPort() {
        return Integer.parseInt(System.getProperty("serverPort", "20000"));
    }

    public static String getServerHost() {
        return System.getProperty("serverHost", "localhost");
    }

    public static Rectangle[] getWindowBounds(String[] args) {
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
                if (arg.startsWith("fullscreen")) {
                    GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                    GraphicsDevice screenDevice = screenDevices[Integer.valueOf(arg.substring("fullscreen".length()))];
                    Rectangle bounds = screenDevice.getDefaultConfiguration().getBounds();
                    configurations[i] = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
                } else {
                    String[] part = arg.split(",");
                    configurations[i] = new Rectangle(
                            Integer.parseInt(part[0]),
                            Integer.parseInt(part[1]),
                            Integer.parseInt(part[2]),
                            Integer.parseInt(part[3]));
                }
            }
        }
        return configurations;
    }
}
