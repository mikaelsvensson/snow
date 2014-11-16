package snow;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class SceneRegionPanel extends JPanel implements WeatherController.Listener {
    private static final int HIDE_HELP_DELAY = 3000;
    private int fps;
    private long lastTime;
    private Rectangle2D.Double regionVisible;
    private double widthFactor;
    private double heightFactor;
    private Rectangle2D.Double regionClip;
    private double windyness = StrictMath.toRadians(50.0);
    private long lastKeyPressTimeStamp;

    public SceneRegionPanel() {
        setDoubleBuffered(true);
        WeatherController.getInstance().addListener(this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                lastKeyPressTimeStamp = System.currentTimeMillis();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.fill(getBounds());

        if (regionVisible != null) {
            AffineTransform oldTransform = g2d.getTransform();
            SceneObject[] sceneObjects = WeatherController.getInstance().getSceneObjects();
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


        if (System.currentTimeMillis() - lastKeyPressTimeStamp < HIDE_HELP_DELAY) {
            drawHelp(g2d);
        }
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
        drawKeyValueString(g2d, y += 20, "Antal objekt", String.valueOf(WeatherController.getInstance().getSceneObjects().length));

        for (KeyStroke key : getInputMap().keys()) {
            drawKeyValueString(g2d, y += 20, KeyEvent.getKeyText(key.getKeyCode()), getActionMap().get(getInputMap().get(key)).getValue(Action.NAME).toString());
        }

    }

    private void drawKeyValueString(Graphics2D g2d, int y, String key, String value) {
        g2d.drawString(key, 10, y);
        g2d.drawString(value, 100, y);
    }

    private void onWindowGeometryChange() {
        Dimension size = getSize();
        widthFactor = size.width / regionVisible.width;
        heightFactor = size.height / regionVisible.height;
    }

    @Override
    public void onSceneChange() {
        repaint();
    }

    public void setRegion(Rectangle2D.Double region) {
        SceneObject[] sceneObjects = WeatherController.getInstance().getSceneObjects();
        int maxObjectHeight = 0;
        int maxObjectWidth = 0;
        for (SceneObject sceneObject : sceneObjects) {
            maxObjectHeight = Math.max(sceneObject.heightPixels, maxObjectHeight);
            maxObjectWidth = Math.max(sceneObject.widthPixels, maxObjectWidth);
        }
        double marginX = maxObjectWidth * region.width / getWidth();
        double marginY = maxObjectHeight * region.height / getHeight();
        this.regionVisible = region;
        this.regionClip = new Rectangle2D.Double(region.x - marginX, region.y - marginY, region.width + marginX + marginX, region.height + marginY + marginY);
        onWindowGeometryChange();
        repaint();
    }

    public Rectangle2D.Double getRegion() {
        return regionVisible;
    }
}
