package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SlimFrame extends JFrame {
    private Point mouseDownCompCoords;

    protected SlimFrame() throws HeadlessException {
        setUndecorated(true);
        initDragWindowSupport();
    }

    /**
     * Thanks to http://stackoverflow.com/a/16046943
     */
    void initDragWindowSupport() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

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
}
