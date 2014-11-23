package snow.weather;

import snow.SlimFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

class WeatherFrame extends SlimFrame {
    private final SceneRegionPanel sceneRegionPanel;

    public WeatherFrame() throws HeadlessException {
        sceneRegionPanel = new SceneRegionPanel();
        sceneRegionPanel.setPreferredSize(new Dimension(600, 600));
        setContentPane(sceneRegionPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();

        registerAction(KeyEvent.VK_INSERT, new AbstractAction("Klona fönster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowController.getInstance().showNewWindow(getX() + 10, getY() + 10, getWidth(), getHeight());
            }
        });
        registerAction(KeyEvent.VK_PAGE_UP, new AbstractAction("Fler objekt") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherController.getInstance().changeObjectCount(10);
            }
        });
        registerAction(KeyEvent.VK_PAGE_DOWN, new AbstractAction("Färre objekt") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherController.getInstance().changeObjectCount(-10);
            }
        });
        registerAction(KeyEvent.VK_S, new AbstractAction("Långsammare snöfall") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherController.getInstance().changeFallingObjectSlowness(1);
            }
        });
        registerAction(KeyEvent.VK_W, new AbstractAction("Snabbare snöfall") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherController.getInstance().changeFallingObjectSlowness(-1);
            }
        });
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        sceneRegionPanel.setName(name);
    }

    void recalibratePanel(Rectangle sceneBounds) {
        if (isVisible()) {
            Rectangle frameBoundsWithinScene = getBounds();

            frameBoundsWithinScene.translate(-sceneBounds.x, -sceneBounds.y);

            Rectangle2D.Double region = new Rectangle.Double(
                    (double) frameBoundsWithinScene.x / sceneBounds.width,
                    (double) frameBoundsWithinScene.y / sceneBounds.height,
                    (double) frameBoundsWithinScene.width / sceneBounds.width,
                    (double) frameBoundsWithinScene.height / sceneBounds.height);

            sceneRegionPanel.setRegion(region);
        }
    }
}
