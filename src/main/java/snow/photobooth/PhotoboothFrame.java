package snow.photobooth;

import snow.SlimFrame;
import snow.computervision.ImageMatrixView;

import java.awt.image.BufferedImage;

class PhotoboothFrame extends SlimFrame {

    private final ImageMatrixView facePanel;

    public PhotoboothFrame() {
        facePanel = new ImageMatrixView();

        setContentPane(facePanel);

        pack();
    }

    public void updateView(BufferedImage image, String... message) {
        facePanel.setImage(image);
        facePanel.setMessage(message);
    }
}
