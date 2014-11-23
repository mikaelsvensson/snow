package snow;

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
}
