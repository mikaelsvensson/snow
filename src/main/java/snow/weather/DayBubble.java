package snow.weather;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;

public class DayBubble extends FallingSceneObject {

    private static final int BASE_FONT_SIZE = 200;
    private static final Font FONT = new Font(Font.SERIF, Font.PLAIN, BASE_FONT_SIZE);
    private static final Font FONT_SMALL = new Font(Font.SERIF, Font.PLAIN, BASE_FONT_SIZE / 4);
    private static final String SUBTITLE = "dagar till jul";
    private static final Color PINK = new Color(176, 19, 0);
    private FontMetrics fontMetrics;
    private FontMetrics fontMetricsSmall;
    private Calendar christmasEve;
    private Rectangle2D textSmallBounds;

    public DayBubble(double z, Rectangle sceneBounds) {
        super((int) (BASE_FONT_SIZE * 1.7), (int) (BASE_FONT_SIZE * 1.7), z, sceneBounds);
        rotationDirection = 0;
    }

    @Override
    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        BufferedImage img = createEmptyImage(requestedWidthPixels, requestedHeightPixels);
        if (christmasEve == null) {
            christmasEve = Calendar.getInstance();
            christmasEve.set(2014, Calendar.DECEMBER, 24, 0, 0, 0);
        }
        int dayOfYearChristmasEve = christmasEve.get(Calendar.DAY_OF_YEAR);
        int dayOfYearToday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (dayOfYearToday < dayOfYearChristmasEve) {
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(PINK);
            String daysLeft = String.valueOf(dayOfYearChristmasEve - dayOfYearToday);
            if (fontMetrics == null) {
                g.setFont(FONT);
                fontMetrics = g.getFontMetrics();
            }
            if (fontMetricsSmall == null) {
                g.setFont(FONT_SMALL);
                fontMetricsSmall = g.getFontMetrics();
                textSmallBounds = fontMetricsSmall.getStringBounds(SUBTITLE, g);
            }
            Rectangle2D textBounds = fontMetrics.getStringBounds(daysLeft, g);
            g.setFont(FONT);
            float daysY = (float) (requestedHeightPixels / 2 - textBounds.getCenterY());
            g.drawString(daysLeft, (float) (requestedWidthPixels / 2 - textBounds.getCenterX()), daysY);
            g.setFont(FONT_SMALL);
            g.drawString(SUBTITLE, (float) (requestedWidthPixels / 2 - textSmallBounds.getCenterX()), (float) (daysY + textSmallBounds.getHeight()));
        }
        return img;
    }
}
