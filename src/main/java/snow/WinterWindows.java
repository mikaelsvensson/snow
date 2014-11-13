package snow;

import javax.swing.*;

/**
 * Hello world!
 */
public class WinterWindows {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                WindowController wc = WindowController.getInstance();
                for (String arg : args) {
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
