package snow;

import snow.computervision.ComputerVision;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class WindowController {
    private static WindowController instance;
    private List<WeatherFrame> windows = new ArrayList<>();

    private WindowController() {
    }

    public static WindowController getInstance() {
        if (instance == null) {
            synchronized (WindowController.class) {
                if (instance == null) {
                    instance = new WindowController();
                }
            }
        }
        return instance;
    }

    synchronized void showNewWindow(int x, int y, int width, int height, String name) {
        final WeatherFrame frame = new WeatherFrame();
        frame.setName(name != null ? name : "Window " + windows.size());
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windows.remove(frame);
                if (windows.size() < 1) {
                    WeatherController.getInstance().stop();
                    ComputerVision.getInstance().stop();
                } else {
                    recalculateSceneObjects();
                }
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                recalculateSceneObjects();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                recalculateSceneObjects();
            }
        });
        initDrawWindowSupport(frame);
        windows.add(frame);
    }

    private void recalculateSceneObjects() {
        Rectangle sceneBounds = getSceneBounds();
        WeatherController.getInstance().setSceneBounds(sceneBounds);
        for (WeatherFrame window : windows) {
            window.recalibratePanel(sceneBounds);
        }
    }

    private Rectangle getSceneBounds() {
        Rectangle2D bounds = null;
        for (int i = 0; i < windows.size(); i++) {
            Rectangle windowBounds = windows.get(i).getBounds();
            if (i == 0) {
                bounds = windowBounds;
            } else {
                bounds = bounds.createUnion(windowBounds);
            }
        }
        return bounds.getBounds();
    }

    private Point mouseDownCompCoords;

    /**
     * Thanks to http://stackoverflow.com/a/16046943
     *
     * @param frame
     */
    private void initDrawWindowSupport(final WeatherFrame frame) {
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords = null;
            }
        });

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });
    }

}
