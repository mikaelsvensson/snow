package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

class WeatherFrame extends JFrame {
    private final SnowPanel snowPanel;


    public WeatherFrame() throws HeadlessException {
        setUndecorated(true);
        snowPanel = new SnowPanel();
        snowPanel.setPreferredSize(new Dimension(600, 600));
        setContentPane(snowPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e);
                super.keyPressed(e);
            }
        });
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        snowPanel.setName(name);
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
                WindowController.getInstance().showNewWindow(getX() + 10, getY() + 10, getWidth(), getHeight(), null);
                break;
            case KeyEvent.VK_DELETE:
                dispose();
                break;
        }
    }

    void moveFrame(int deltaX, int deltaY) {
        setLocation(getLocation().x + deltaX, getLocation().y + deltaY);
    }

    void recalibrateSnowPanel(Rectangle sceneBounds) {
        if (isVisible()) {
            Rectangle frameBoundsWithinScene = getBounds();

            frameBoundsWithinScene.translate(-sceneBounds.x, -sceneBounds.y);

            Rectangle2D.Double region = new Rectangle.Double(
                    (double) frameBoundsWithinScene.x / sceneBounds.width,
                    (double) frameBoundsWithinScene.y / sceneBounds.height,
                    (double) frameBoundsWithinScene.width / sceneBounds.width,
                    (double) frameBoundsWithinScene.height / sceneBounds.height);

            snowPanel.setRegion(region);
        }
    }

/*
    private GraphicsDevice getGraphicsDevice() {
        GraphicsConfiguration curConf = getGraphicsConfiguration();
        GraphicsDevice device = null;
        for (GraphicsDevice graphicsDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            for (GraphicsConfiguration conf : graphicsDevice.getConfigurations()) {
                if (conf.equals(curConf)) {
                    device = graphicsDevice;
                    break;
                }
            }
        }
        return device;
    }
*/

}
