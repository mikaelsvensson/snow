package snow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SnowFlake extends FallingSceneObject {

    private static final Color WHITE = new Color(255, 255, 255);

    public SnowFlake(int widthPixels, int heightPixels, double z, Rectangle sceneBounds) {
        super(widthPixels, heightPixels, z, sceneBounds);
    }

    protected BufferedImage createImage(int requestedWidthPixels, int requestedHeightPixels) {
        BufferedImage img = createEmptyImage(requestedWidthPixels, requestedHeightPixels);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double baseLength = Util.random(0.2, 1.0);
        int thickness = 1 + (int) (Util.random(0.05, 0.1) * requestedWidthPixels * baseLength);
        g.setColor(WHITE);
        int length = (int) (baseLength * requestedHeightPixels / 2);
        int numberOfBranches = length / thickness / 2;
        int[] branchPositions = new int[numberOfBranches];
        for (int i = 0; i < branchPositions.length; i++) {
            branchPositions[i] = (int) (Util.random(0.1, 0.8) * length);
        }
        int centerX = requestedWidthPixels / 2;
        int centerY = requestedHeightPixels / 2;
        g.setStroke(new BasicStroke(thickness));
        for (int i = 0; i < 6; i++) {
            g.setTransform(AffineTransform.getRotateInstance(i * Math.PI / 3, centerX, centerY));
            g.fillRect(centerX - (thickness / 2), centerY - length, thickness, length);
            for (int branchPosition : branchPositions) {
                int branchLength = (int) (branchPosition * 0.3);
                g.drawLine(centerX, centerY - branchPosition, centerX - branchLength, centerY - branchPosition - branchLength);
                g.drawLine(centerX, centerY - branchPosition, centerX + branchLength, centerY - branchPosition - branchLength);
            }
        }
        return img;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final int COL_AND_ROW_COUNT = 5;
                final int SIZE = 200;

                JFrame frame = new JFrame();
                final JPanel panel = new JPanel(new GridLayout(COL_AND_ROW_COUNT, COL_AND_ROW_COUNT));
                panel.setBackground(Color.black);
                for (int x = 0; x < COL_AND_ROW_COUNT; x++) {
                    for (int y = 0; y < COL_AND_ROW_COUNT; y++) {
                        panel.add(new JLabel(new ImageIcon(), SwingConstants.CENTER), x * y);
                    }
                }
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        initImages(COL_AND_ROW_COUNT, SIZE, panel);
                    }
                });
                initImages(COL_AND_ROW_COUNT, SIZE, panel);
                frame.setContentPane(panel);
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }

            private void initImages(int COL_AND_ROW_COUNT, int SIZE, JPanel panel) {
                for (int i = 0; i < COL_AND_ROW_COUNT * COL_AND_ROW_COUNT; i++) {
                    JLabel component = (JLabel) panel.getComponents()[i];
                    component.setIcon(new ImageIcon(new SnowFlake(SIZE, SIZE, 0, new Rectangle()).image));
                }
            }

        });
    }
}
