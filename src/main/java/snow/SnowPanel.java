package snow;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class SnowPanel extends JPanel implements WeatherController.Listener {
    private int fps;
    private long lastTime;
    private Rectangle2D.Double regionVisible;
    private double widthFactor;
    private double heightFactor;
    private Rectangle2D.Double regionClip;
    private double windyness = StrictMath.toRadians(50.0);

    public SnowPanel() {
        setDoubleBuffered(true);
        WeatherController.getInstance().addListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.fill(getBounds());

        if (regionVisible != null) {
            AffineTransform oldTransform = g2d.getTransform();
            SceneObject[] sceneObjects = WeatherController.getInstance().getSnowFlakes();
            for (SceneObject sceneObject : sceneObjects) {
                if (regionClip.contains(sceneObject.x, sceneObject.y)) {
                    AffineTransform transform = AffineTransform.getRotateInstance(StrictMath.toRadians(sceneObject.rotation), sceneObject.widthPixels / 2, sceneObject.heightPixels / 2);
//            transform.rotate(windyness);
                    g2d.setTransform(AffineTransform.getTranslateInstance(
                            (sceneObject.x - regionVisible.x) * widthFactor - sceneObject.widthPixels / 2,
                            (sceneObject.y - regionVisible.y) * heightFactor - sceneObject.heightPixels / 2));
                    g2d.drawImage(sceneObject.image, transform, this);
                }
            }
            g2d.setTransform(oldTransform);
        } else {
            g2d.setColor(Color.red);
            g2d.drawString("No regionVisible specified", 0, 20);
        }

        long now = System.currentTimeMillis();
        long delay = now - lastTime;
        if (now / 1000 != lastTime / 1000) {
            // Calculate fps count once every second
            fps = (int) (1000.0 / delay);
        }
        lastTime = now;
        g2d.setColor(Color.red);
        g2d.drawString(getName() + ": " + String.valueOf(fps) + " fps", 0, 10);
    }

    private void onWindowGeometryChange() {
        Dimension size = getSize();
        widthFactor = size.width / regionVisible.width;
        heightFactor = size.height / regionVisible.height;
    }

    @Override
    public void onSnowFlakeChange() {
        repaint();
    }

    public void setRegion(Rectangle2D.Double region) {
        SceneObject[] sceneObjects = WeatherController.getInstance().getSnowFlakes();
        int maxSnowFlakeHeight = 0;
        int maxSnowFlakeWidth = 0;
        for (SceneObject sceneObject : sceneObjects) {
            maxSnowFlakeHeight = Math.max(sceneObject.heightPixels, maxSnowFlakeHeight);
            maxSnowFlakeWidth = Math.max(sceneObject.widthPixels, maxSnowFlakeWidth);
        }
        double marginX = maxSnowFlakeWidth * region.width / getWidth();
        double marginY = maxSnowFlakeHeight * region.height / getHeight();
        this.regionVisible = region;
        this.regionClip = new Rectangle2D.Double(region.x - marginX, region.y - marginY, region.width + marginX + marginX, region.height + marginY + marginY);
        onWindowGeometryChange();
        repaint();
    }

    public Rectangle2D.Double getRegion() {
        return regionVisible;
    }
}
