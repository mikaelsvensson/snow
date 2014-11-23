package snow.computervision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Source: https://sites.google.com/site/pdopencvjava/webcam/02-continually-detect-face-in-webcam
 */
public class ComputerVision {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static ComputerVision instance = null;
    private Thread imageProviderThread;
    private ImageProvider imageProvider;
    private final List<Listener> listeners = new ArrayList<>();

    private ComputerVision() {
    }

    public static ComputerVision getInstance() {
        if (instance == null) {
            synchronized (ComputerVision.class) {
                if (instance == null) {
                    instance = new ComputerVision();
                }
            }
        }
        return instance;
    }

    public void addImageAnalyser(ImageAnalyzer imageAnalyzer) {
        getImageProvider().addImageAnalyser(imageAnalyzer);
    }

    void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void start() {
        imageProviderThread = new Thread(getImageProvider());
//        imageProviderThread.setPriority(Thread.MIN_PRIORITY);
        imageProviderThread.start();
    }

    private ImageProvider getImageProvider() {
        if (imageProvider == null) {
            imageProvider = new ImageProvider();
        }
        return imageProvider;
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        final ImageMatrixView facePanel = new ImageMatrixView();

        ComputerVision.getInstance().start();
        ComputerVision.getInstance().addImageAnalyser(new SantaHatter("D:\\Dokument\\Utveckling\\snow\\src\\main\\resources\\haarcascade_frontalface_alt.xml", true));
        ComputerVision.getInstance().addListener(new Listener() {
            @Override
            public void onImageUpdate(BufferedImage image) {
                facePanel.setImage(image);
            }
        });

        JFrame frame = new JFrame("Face Detection in Web Cam");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.add(facePanel);

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ComputerVision.getInstance().stop();
            }
        });
    }

    public void stop() {
        if (imageProviderThread != null) {
            imageProviderThread.interrupt();
            try {
                imageProviderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private class ImageProvider implements Runnable {
        private final List<ImageAnalyzer> imageAnalyzers = new ArrayList<>();

        @Override
        public void run() {
            final VideoCapture webCam = new VideoCapture(0);

            Mat webcamImage = new Mat();
            if (webCam.isOpened()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    webCam.read(webcamImage);
                    if (!webcamImage.empty()) {
                        try {
                            for (ImageAnalyzer imageAnalyzer : imageAnalyzers) {
                                imageAnalyzer.process(webcamImage);
                            }
                            BufferedImage image = getBufferedImage(webcamImage);
                            for (ImageAnalyzer imageAnalyzer : imageAnalyzers) {
                                imageAnalyzer.postProcess(image);
                            }
                            fireOnImageUpdate(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                webCam.release();
                System.out.println("Done with webcam");
            }
        }

        public BufferedImage getBufferedImage(Mat image) throws IOException {

            MatOfByte mb = new MatOfByte();
//            Highgui.imencode(".jpg", image, mb);
            Highgui.imencode(".png", image, mb);
            return ImageIO.read(new ByteArrayInputStream(mb.toArray()));
        }

        public void addImageAnalyser(ImageAnalyzer imageAnalyzer) {
            imageAnalyzers.add(imageAnalyzer);
        }
    }

    private void fireOnImageUpdate(BufferedImage image) {
        for (Listener listener : listeners) {
            listener.onImageUpdate(image);
        }
    }

    public static interface Listener {
        void onImageUpdate(BufferedImage image);
    }
}
