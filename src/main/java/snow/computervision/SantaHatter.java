package snow.computervision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SantaHatter implements ImageAnalyzer {

    private static final int NUMBER_OF_FRAMES_TO_HOLD_STILL_FOR = 5;
    private final CascadeClassifier detector;
    private Rect[][] history = new Rect[NUMBER_OF_FRAMES_TO_HOLD_STILL_FOR][];
    private int historyPos = 0;
    private BufferedImage santaHatImage;
    private FaceStatus faceStatus;

    public static enum FaceStatus {
        NO,
        YES_MOVING,
        YES_STATIC
    }

    private List<Listener> listeners = new ArrayList<>();

    public SantaHatter(String faceDetectionConfigurationFilePath) {
        try {
            santaHatImage = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("santa_hat.png"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        if (!new File(faceDetectionConfigurationFilePath).isFile()) {
            throw new IllegalArgumentException("Path to face detection configuration file is invalid");
        }
        detector = new CascadeClassifier(faceDetectionConfigurationFilePath);
    }

    @Override
    public void process(Mat image) {
        Rect[] rects = detectRects(image);
        history[historyPos] = rects;
        if (rects.length >= 1) {
            fireFaceDetected();

            faceStatus = FaceStatus.YES_STATIC;
            for (int i = 0; i < history.length; i++) {
                Rect[] histRects = history[(historyPos + i) % history.length];
                Rect[] prevHistRects = history[(historyPos + i - 1 + history.length) % history.length];
                Rect histRect = getLargest(histRects);
                Rect prevHistRect = getLargest(prevHistRects);
                if (histRect != null && prevHistRect != null) {
                    int avgWidth = histRect.width / 2 + prevHistRect.width / 2;
                    int avgHeight = histRect.height / 2 + prevHistRect.height / 2;
                    if (1.0 * Math.abs(histRect.x - prevHistRect.x) / avgWidth >= 0.1 || 1.0 * Math.abs(histRect.y - prevHistRect.y) / avgHeight >= 0.1) {
                        faceStatus = FaceStatus.YES_MOVING;
                    }
                } else {
                    faceStatus = FaceStatus.YES_MOVING;
                }
            }
        } else {
            faceStatus = FaceStatus.NO;
        }
    }

    private void fireFaceDetected() {
        for (Listener listener : listeners) {
            listener.onFaceDetected();
        }
    }

    private Rect getLargest(Rect[] rects) {
        Rect maxRect = null;
        if (rects != null) {
            for (Rect rect : rects) {
                if (maxRect == null || rect.size().area() > maxRect.size().area()) {
                    maxRect = rect;
                }
            }
        }
        return maxRect;
    }

    public Rect[] detectRects(Mat image) {
        Mat grey = new Mat();
//            Mat rgba = new Mat();
//            image.copyTo(rgba);
//            Imgproc.cvtColor(rgba, grey, Imgproc.COLOR_BGR2GRAY);
        image.copyTo(grey);
        Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grey, grey);

        MatOfRect faces = new MatOfRect();
        detector.detectMultiScale(grey, faces);
        int i = 1;
        return faces.toArray();
    }

    @Override
    public void postProcess(BufferedImage image) {
        Rect[] rects = history[historyPos];
        Rect rect = getLargest(rects);
        if (rect != null) {
            Graphics2D g = image.createGraphics();
            g.setColor(Color.blue);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
            AffineTransform transform = new AffineTransform();
            double sx = 1.0 * (rect.width * 1.4) / santaHatImage.getWidth();
            double sy = 1.0 * rect.height / santaHatImage.getHeight();
            transform.scale(sx, sy);
            transform.translate((rect.x - (.1 * rect.width)) / sx, (rect.y - rect.height + 0.3 * rect.height) / sy);
            g.drawImage(santaHatImage, transform, null);
            g.dispose();
        }
        historyPos = (historyPos + 1) % history.length;

        if (faceStatus == FaceStatus.YES_STATIC) {
            fireStaticDetected();
        }

        firePostProcessed(faceStatus, image);
    }

    private void fireStaticDetected() {
        for (Listener listener : listeners) {
            listener.onStaticFaceDetected();
        }
    }

    private void firePostProcessed(FaceStatus faceStatus, BufferedImage image) {
        for (Listener listener : listeners) {
            listener.onPostProcessed(faceStatus, image);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static interface Listener {
        void onFaceDetected();

        void onStaticFaceDetected();

        void onPostProcessed(FaceStatus faceStatus, BufferedImage image);
    }
}
