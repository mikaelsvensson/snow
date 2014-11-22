package snow.computervision;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

public interface ImageAnalyzer {
    void process(Mat image);

    void postProcess(BufferedImage image);
}
