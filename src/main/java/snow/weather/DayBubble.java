package snow.weather;

import snow.Util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;

public class DayBubble extends FallingSceneObject {

    private static final String SUBTITLE = "dagar till jul";
    private Calendar christmasEve;

    public DayBubble(double z, Rectangle sceneBounds) {
        super(getBaseFontSize(sceneBounds), getBaseFontSize(sceneBounds), z, sceneBounds);
        rotationDirection = 0;
    }

    private static int getBaseFontSize(Rectangle sceneBounds) {
        return (sceneBounds.width / 4);
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
            g.setColor(Util.DARK_CHRISTMAS_RED);
            String daysLeft = String.valueOf(dayOfYearChristmasEve - dayOfYearToday);
            Font font = new Font(Font.SERIF, Font.PLAIN, (int) (0.7 * requestedWidthPixels));

            Font fontSmall = new Font(Font.SERIF, Font.PLAIN, (int) (0.15 * requestedWidthPixels));
            g.setFont(font);
            Rectangle2D textBounds = g.getFontMetrics().getStringBounds(daysLeft, g);
            float daysY = (float) (requestedHeightPixels / 2 - textBounds.getCenterY());
            g.drawString(daysLeft, (float) (requestedWidthPixels / 2 - textBounds.getCenterX()), daysY);

            g.setFont(fontSmall);
            Rectangle2D textSmallBounds = g.getFontMetrics().getStringBounds(SUBTITLE, g);
            g.drawString(SUBTITLE, (float) (requestedWidthPixels / 2 - textSmallBounds.getCenterX()), (float) (daysY + textSmallBounds.getHeight()));
        }
        return img;
    }
}
