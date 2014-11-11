package snow;

import javax.swing.*;

/**
 * Hello world!
 */
public class WinterWindows {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                WeatherController.getInstance().start();

                WeatherFrame.showNewWindow(100, 100, 500, 500);
                WeatherFrame.showNewWindow(300, 300, 500, 500);
                WeatherFrame.showNewWindow(1000, 1000, 500, 200);
                WeatherFrame.showNewWindow(-100, 800, 300, 100);
            }

        });
    }

}
