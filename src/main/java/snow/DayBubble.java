package snow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;

public class DayBubble extends FallingSceneObject {

    private static final Font FONT = new Font(Font.SERIF, Font.PLAIN, 100);

    public DayBubble(int widthPixels, int heightPixels, double blur, Rectangle sceneBounds) {
        super(widthPixels, heightPixels, blur, sceneBounds);
    }

    @Override
    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        BufferedImage img = createEmptyImage(requestedWidthPixels, requestedHeightPixels);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.red);
        g.setFont(FONT);
        String text = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Rectangle2D textBounds = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text, (float)(requestedWidthPixels / 2 - textBounds.getCenterX()), (float) (requestedHeightPixels / 2 - textBounds.getCenterY()));
        return img;
    }
}
