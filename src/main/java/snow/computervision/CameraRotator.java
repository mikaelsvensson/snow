package snow.computervision;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

public class CameraRotator implements ImageAnalyzer {
    public Rotation rotation;

    public CameraRotator(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public void process(Mat image) {
        switch (rotation) {
            case CCW90:
                // 90 degrees counter clock-wise
                Core.transpose(image, image);
                break;
            case CW90:
                // 90 degrees clock-wise
                Core.transpose(image, image);
                Core.flip(image, image, -1);
                break;
        }

    }

    @Override
    public void postProcess(BufferedImage image) {
    }
}
