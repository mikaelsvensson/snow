package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

class WeatherFrame extends JFrame {
    private final SceneRegionPanel sceneRegionPanel;

    public WeatherFrame() throws HeadlessException {
        setUndecorated(true);
        sceneRegionPanel = new SceneRegionPanel();
        sceneRegionPanel.setPreferredSize(new Dimension(600, 600));
        setContentPane(sceneRegionPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();

//        System.out.println(KeyEvent.getKeyText(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0).getKeyCode()));

        registerAction(KeyEvent.VK_LEFT, new AbstractAction("Flytta fönster till vänster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrame(-50, 0);
            }
        });
        registerAction(KeyEvent.VK_RIGHT, new AbstractAction("Flytta fönster till höger") {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrame(50, 0);
            }
        });
        registerAction(KeyEvent.VK_UP, new AbstractAction("Flytta fönster uppåt") {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrame(0, -50);
            }
        });
        registerAction(KeyEvent.VK_DOWN, new AbstractAction("Flytta fönster nedåt") {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrame(0, 50);
            }
        });
        registerAction(KeyEvent.VK_INSERT, new AbstractAction("Klona fönster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowController.getInstance().showNewWindow(getX() + 10, getY() + 10, getWidth(), getHeight(), null);
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
        registerAction(KeyEvent.VK_ESCAPE, new AbstractAction("Stäng fönster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void registerAction(int keyCode, AbstractAction action) {
        sceneRegionPanel.getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), action.getValue(Action.NAME));
        sceneRegionPanel.getActionMap().put(action.getValue(Action.NAME), action);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        sceneRegionPanel.setName(name);
    }

    void moveFrame(int deltaX, int deltaY) {
        setLocation(getLocation().x + deltaX, getLocation().y + deltaY);
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
