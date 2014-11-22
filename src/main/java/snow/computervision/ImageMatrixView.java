package snow.computervision;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ImageMatrixView extends JPanel {
    private static final Font OVERLAY_FONT = new Font("Arial", Font.BOLD, 100);
    private BufferedImage buf;
    private String[] message;

    public void setImage(BufferedImage image) {
        buf = image;
        System.out.println("Image updated");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (buf != null) {
            g.drawImage(buf, 10, 10, buf.getWidth(), buf.getHeight(), null);
        }
        if (message != null) {
            g2d.setFont(OVERLAY_FONT);
            g2d.setColor(Color.white);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (int i = 0; i < message.length; i++) {
                String line = message[i];
                FontMetrics metrics = g2d.getFontMetrics(OVERLAY_FONT);
                Rectangle2D bounds = metrics.getStringBounds(line, g2d);
                g2d.drawString(line,
                        (getWidth() / 2) - (int) (bounds.getWidth() / 2),
                        (getHeight() / 2) + (metrics.getHeight() / 2) - (message.length * metrics.getHeight() / 2) + (i * metrics.getHeight()));

            }
        }
    }

    public void setMessage(String... message) {
        this.message = message;
    }

}
