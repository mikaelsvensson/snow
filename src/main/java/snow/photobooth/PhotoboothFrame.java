package snow.photobooth;

import snow.SlimFrame;
import snow.computervision.ImageMatrixView;

import java.awt.image.BufferedImage;

class PhotoboothFrame extends SlimFrame {

    private final ImageMatrixView facePanel;
    private boolean messageVisible;

    public PhotoboothFrame() {
        facePanel = new ImageMatrixView();

        setContentPane(facePanel);

        pack();
    }

    public void updateView(BufferedImage image, double waitProgress, String... message) {
        facePanel.setImage(image);
        if (messageVisible) {
            facePanel.setMessage(message);
            facePanel.setWaitProgress(waitProgress);
        }
    }

    public void setMessageVisible(boolean messageVisible) {
        this.messageVisible = messageVisible;
    }

    public boolean isMessageVisible() {
        return messageVisible;
    }
}
