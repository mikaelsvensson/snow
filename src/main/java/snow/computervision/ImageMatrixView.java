package snow.computervision;

import snow.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ImageMatrixView extends JPanel {
    private static final Font OVERLAY_FONT = new Font(Font.SERIF, Font.BOLD, 100);
    private static final Color FOREGROUND_COLOR = new Color(255, 255, 255, (int) (255 * 0.7));
    private static final Color BACKGROUND_COLOR = new Color(
            Util.DARK_CHRISTMAS_RED.getRed(),
            Util.DARK_CHRISTMAS_RED.getGreen(),
            Util.DARK_CHRISTMAS_RED.getBlue(),
            (int) (255 * 0.7));
    private BufferedImage buf;
    private String[] message;
    private double waitProgress;

    public void setImage(BufferedImage image) {
        int height = getHeight();
        int width = getWidth();
        if (image.getWidth() != width || image.getHeight() != height) {
            buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();

            double heightRatio = 1.0 * height / image.getHeight();
            double widthRatio = 1.0 * width / image.getWidth();
            double resizeRatio = Math.min(heightRatio, widthRatio);
            int newImageHeight = (int) (resizeRatio * image.getHeight());
            int newImageWidth = (int) (resizeRatio * image.getWidth());

            g.drawImage(image, (width - newImageWidth) / 2, (height - newImageHeight) / 2, newImageWidth, newImageHeight, null);
            g.dispose();
        } else {
            buf = image;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (buf != null) {
            paintImage(g2d);
        }
        if (waitProgress >= 0.1) {
            paintWaitProgressClock(g2d);
        }
        if (message != null) {
            paintMessage(g2d);
        }
    }

    private void paintWaitProgressClock(Graphics2D g2d) {
        int diameter = (int) (0.5 * Math.min(getWidth(), getHeight()));
        g2d.setColor(FOREGROUND_COLOR);
        g2d.fillArc(getWidth() / 2 - diameter / 2, getHeight() / 2 - diameter / 2, diameter, diameter, 90, -(int) (waitProgress * 360));
    }

    private void paintMessage(Graphics2D g2d) {
        FontMetrics metrics = g2d.getFontMetrics(OVERLAY_FONT);
        g2d.setFont(OVERLAY_FONT);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Rectangle2D[] lineBounds = new Rectangle2D[message.length];
        Point[] xyPoints = new Point[message.length];
        for (int i = 0; i < message.length; i++) {
            String line = message[i];
            lineBounds[i] = metrics.getStringBounds(line, g2d);
            xyPoints[i] = new Point(
                    (getWidth() / 2) - (int) (lineBounds[i].getWidth() / 2),
                    (getHeight() / 2) + (metrics.getHeight() / 2) - (message.length * metrics.getHeight() / 2) + (i * metrics.getHeight()));
        }
        g2d.setColor(BACKGROUND_COLOR);
        for (int i = 0; i < message.length; i++) {
            Rectangle2D bounds = lineBounds[i];

            g2d.fillRect(xyPoints[i].x - (int) (0.04 * bounds.getWidth()), xyPoints[i].y - (int) (0.75 * bounds.getHeight()), (int) (1.08 * bounds.getWidth()), (int) bounds.getHeight());
        }
        g2d.setColor(FOREGROUND_COLOR);
        for (int i = 0; i < message.length; i++) {
            String line = message[i];

            g2d.drawString(line, xyPoints[i].x, xyPoints[i].y);
        }
    }

    private void paintImage(Graphics g) {
        g.drawImage(buf, 0, 0, getWidth(), getHeight(), null);
    }

    public void setMessage(String... message) {
        this.message = message;
    }

    public void setWaitProgress(double waitProgress) {
        this.waitProgress = waitProgress;
    }

    public double getWaitProgress() {
        return waitProgress;
    }
}
