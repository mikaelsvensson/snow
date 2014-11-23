package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SlimFrame extends JFrame {
    private Point mouseDownCompCoords;

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
                    setExtendedState(getExtendedState() == NORMAL ? MAXIMIZED_BOTH : NORMAL);
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
    }

    protected void moveFrame(int deltaX, int deltaY) {
        setLocation(getLocation().x + deltaX, getLocation().y + deltaY);
    }

    protected void registerAction(int keyCode, AbstractAction action) {
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), action.getValue(Action.NAME));
        getRootPane().getActionMap().put(action.getValue(Action.NAME), action);
    }
}
