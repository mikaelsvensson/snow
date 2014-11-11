package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

class WeatherFrame extends JFrame {
    public static int windowCount;
    private final SnowPanel snowPanel;
    Point mouseDownCompCoords;


    public WeatherFrame() throws HeadlessException {
        setUndecorated(true);
        snowPanel = new SnowPanel();
        snowPanel.setPreferredSize(new Dimension(600, 600));
        setContentPane(snowPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();

        initDrawWindowSupport();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e);
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                recalibrateSnowPanel();
                super.componentMoved(e);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                recalibrateSnowPanel();
                super.componentResized(e);
            }
        });
    }

    /**
     * Thanks to http://stackoverflow.com/a/16046943
     */
    private void initDrawWindowSupport() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });
    }

    private void handleKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveFrame(e.isShiftDown() ? -100 : -20, 0);
                break;
            case KeyEvent.VK_RIGHT:
                moveFrame(e.isShiftDown() ? 100 : 20, 0);
                break;
            case KeyEvent.VK_UP:
                moveFrame(0, e.isShiftDown() ? -100 : -20);
                break;
            case KeyEvent.VK_DOWN:
                moveFrame(0, e.isShiftDown() ? 100 : 20);
                break;
            case KeyEvent.VK_INSERT:
                showNewWindow(getX() + 10, getY() + 10, getWidth(), getHeight());
                break;
            case KeyEvent.VK_DELETE:
                dispose();
                windowCount--;
                if (windowCount < 1) {
                    WeatherController.getInstance().stop();
                }
                break;
        }
    }

    private void moveFrame(int deltaX, int deltaY) {
        setLocation(getLocation().x + deltaX, getLocation().y + deltaY);
    }

    private void recalibrateSnowPanel() {
        if (isVisible()) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Point framePosition = getLocationOnScreen();
            Dimension frameSize = getSize();

            Rectangle2D.Double region = new Rectangle.Double(
                    (double) framePosition.x / screenSize.width,
                    (double) framePosition.y / screenSize.height,
                    (double) frameSize.width / screenSize.width,
                    (double) frameSize.height / screenSize.height);

            snowPanel.setRegion(region);
        }
    }

    static synchronized void showNewWindow(int x, int y, int width, int height) {
        windowCount++;

        WeatherFrame frame = new WeatherFrame();
        frame.setLocation(x, y);
        frame.setSize(width, height);
        frame.setVisible(true);
    }
}
