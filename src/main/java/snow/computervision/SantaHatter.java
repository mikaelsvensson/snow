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

    private final CascadeClassifier detector;
    private Rect[] lastFrameRects = null;
    private FaceStatus faceStatus;
    private long smallFaceMovementsSince = -1;
    private boolean isFrameDrawnAroundFace;
    private Overlay currentOverlay;

    private static class Overlay {
        private double widthFactor = 1.0;
        private double heightFactor = 1.0;
        private double xOffset = 0.0;
        private double yOffset = 0.0;
        private BufferedImage image = null;

        private Overlay(BufferedImage image, double widthFactor, double heightFactor, double xOffset, double yOffset) {
            this.heightFactor = heightFactor;
            this.image = image;
            this.widthFactor = widthFactor;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    private static Overlay[] overlays = null;

    public static enum FaceStatus {
        NO,
        YES_MOVING,
        YES_STATIC
    }

    private final List<Listener> listeners = new ArrayList<>();

    static {
        try {
            Overlay overlayAviatorSunglasses = new Overlay(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("aviator_sunglasses.png")), 1.0, 0.8, 0.0, 0.0);
            Overlay overlayBigWhiteBeard = new Overlay(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("whitebeard.png")), 1.0, 1.0, 0.0, 0.6);
            Overlay overlayMustache = new Overlay(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("mustasch.png")), 0.6, 0.15, 0.2, 0.65);
            Overlay overlaySantaHat = new Overlay(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("santa_hat.png")), 1.4, 1.0, -0.1, -0.7);
            overlays = new Overlay[] {
                    overlayAviatorSunglasses,
                    overlayBigWhiteBeard,
                    overlayBigWhiteBeard,
                    overlayMustache,
                    overlaySantaHat,
                    overlaySantaHat,
                    overlaySantaHat
            };
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public SantaHatter(String faceDetectionConfigurationFilePath, boolean frameDrawnAroundFace) {
        isFrameDrawnAroundFace = frameDrawnAroundFace;
        if (!new File(faceDetectionConfigurationFilePath).isFile()) {
            throw new IllegalArgumentException("Path to face detection configuration file is invalid");
        }
        detector = new CascadeClassifier(faceDetectionConfigurationFilePath);
    }

    @Override
    public void process(Mat image) {
        Rect[] rects = detectRects(image);
        Rect currentFaceRect = getLargest(rects);
        boolean isCurrentFaceBig = currentFaceRect != null && (1.0 * currentFaceRect.height / image.height()) > 0.2;
        if (isCurrentFaceBig) {
            fireFaceDetected();

            Rect previousFaceRect = getLargest(lastFrameRects);
            boolean isPreviousFaceBig = previousFaceRect != null && (1.0 * previousFaceRect.height / image.height()) > 0.2;
            if (isPreviousFaceBig) {
                int avgWidth = currentFaceRect.width / 2 + previousFaceRect.width / 2;
                int avgHeight = currentFaceRect.height / 2 + previousFaceRect.height / 2;
                boolean isFaceMovedX = 1.0 * Math.abs(currentFaceRect.x - previousFaceRect.x) / avgWidth >= 0.1;
                boolean isFaceMovedY = 1.0 * Math.abs(currentFaceRect.y - previousFaceRect.y) / avgHeight >= 0.1;
                if (isPreviousFaceBig && (isFaceMovedX || isFaceMovedY)) {
                    faceStatus = FaceStatus.YES_MOVING;
                    smallFaceMovementsSince = System.currentTimeMillis();
                    currentOverlay = overlays[((int) (Math.random() * overlays.length))];
                } else {
                    // Only small movement since last analyzed frame
                    faceStatus = FaceStatus.YES_STATIC;
                }
            } else {
                // The last detected face was quite small but the currently detected face is large, so the face is moving away from or towards the camera.
                faceStatus = FaceStatus.YES_MOVING;
                smallFaceMovementsSince = System.currentTimeMillis();
                currentOverlay = overlays[((int) (Math.random() * overlays.length))];
            }

            lastFrameRects = rects;
        } else {
            faceStatus = FaceStatus.NO;
            smallFaceMovementsSince = -1;
            lastFrameRects = null;
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

    Rect[] detectRects(Mat image) {
        Mat grey = new Mat();
//            Mat rgba = new Mat();
//            image.copyTo(rgba);
//            Imgproc.cvtColor(rgba, grey, Imgproc.COLOR_BGR2GRAY);
        image.copyTo(grey);
        Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grey, grey);

        MatOfRect faces = new MatOfRect();
        detector.detectMultiScale(grey, faces);
        return faces.toArray();
    }

    @Override
    public void postProcess(BufferedImage image) {
        Rect rect = getLargest(lastFrameRects);
        if (rect != null) {
            Graphics2D g = image.createGraphics();
            if (isFrameDrawnAroundFace) {
                g.setColor(Color.blue);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
            AffineTransform transform = new AffineTransform();
            double sx = 1.0 * (rect.width * currentOverlay.widthFactor) / currentOverlay.image.getWidth();
            double sy = 1.0 * (rect.height * currentOverlay.heightFactor) / currentOverlay.image.getHeight();
            transform.scale(sx, sy);
            transform.translate((rect.x + (currentOverlay.xOffset * rect.width)) / sx, (rect.y + (currentOverlay.yOffset * rect.height)) / sy);
            g.drawImage(currentOverlay.image, transform, null);
            g.dispose();
        }

        if (faceStatus == FaceStatus.YES_STATIC) {
            fireStaticDetected();
        }

        firePostProcessed(faceStatus, image, smallFaceMovementsSince);
    }

    private void fireStaticDetected() {
        for (Listener listener : listeners) {
            listener.onStaticFaceDetected();
        }
    }

    private void firePostProcessed(FaceStatus faceStatus, BufferedImage image, long smallFaceMovementsSince) {
        for (Listener listener : listeners) {
            listener.onPostProcessed(faceStatus, image, smallFaceMovementsSince);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static interface Listener {
        void onFaceDetected();

        void onStaticFaceDetected();

        void onPostProcessed(FaceStatus faceStatus, BufferedImage image, long smallFaceMovementsSince);
    }
}
