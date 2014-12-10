package snow;

import snow.weather.SceneObject;
import snow.weather.WeatherController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SlimFrame extends JFrame {
    private static final int HIDE_HELP_DELAY = 3000;

    private Point mouseDownCompCoords;
    private long lastKeyPressTimeStamp;
    private int fps;
    private long lastTime;

    protected SlimFrame() throws HeadlessException {
        setUndecorated(true);
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
        registerAction(KeyEvent.VK_ESCAPE, new AbstractAction("Stäng fönster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        registerAction(KeyEvent.VK_M, new AbstractAction("Maximiera fönster") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMaximized();
            }
        });
        addMouseListener(new MouseAdapter() {
            /**
             * Used during dragging of window.
             */
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

            /**
             * Used during dragging of window.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximized();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            /**
             * Used during dragging of window.
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });

        getRootPane().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                lastKeyPressTimeStamp = System.currentTimeMillis();
            }
        });
    }

    private void toggleMaximized() {
        setExtendedState(getExtendedState() == NORMAL ? MAXIMIZED_BOTH : NORMAL);
    }

    protected void moveFrame(int deltaX, int deltaY) {
        setLocation(getLocation().x + deltaX, getLocation().y + deltaY);
    }

    protected void registerAction(int keyCode, AbstractAction action) {
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), action.getValue(Action.NAME));
        getRootPane().getActionMap().put(action.getValue(Action.NAME), action);
        setGlassPane(new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                if (System.currentTimeMillis() - lastKeyPressTimeStamp < HIDE_HELP_DELAY) {
                    drawHelp((Graphics2D) g);
                }
            }
        });
        getGlassPane().setVisible(true);
    }

    private void drawHelp(Graphics2D g2d) {
        int y = 0;

        g2d.setColor(Color.red);

        long now = System.currentTimeMillis();
        long delay = now - lastTime;
        if (now / 1000 != lastTime / 1000) {
            // Calculate fps count once every second
            fps = (int) (1000.0 / delay);
        }
        lastTime = now;

        drawKeyValueString(g2d, y += 20, "Fönster", getName());
        drawKeyValueString(g2d, y += 20, "FPS", String.valueOf(fps));
        SceneObject[] sceneObjects = WeatherController.getInstance().getSceneObjects();
        if (sceneObjects != null) {
            drawKeyValueString(g2d, y += 20, "Antal objekt", String.valueOf(sceneObjects.length));
        }

        for (KeyStroke key : getRootPane().getInputMap().keys()) {
            drawKeyValueString(g2d, y += 20, KeyEvent.getKeyText(key.getKeyCode()), getRootPane().getActionMap().get(getRootPane().getInputMap().get(key)).getValue(Action.NAME).toString());
        }

    }

    private void drawKeyValueString(Graphics2D g2d, int y, String key, String value) {
        g2d.drawString(key, 10, y);
        g2d.drawString(value, 100, y);
    }

}
