package snow.photobooth;

import snow.Util;
import snow.computervision.ComputerVision;
import snow.computervision.SantaHatter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PhotoboothApplication implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new PhotoboothApplication());
    }

    @Override
    public void run() {
        final PhotoboothFrame frame = new PhotoboothFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setVisible(true);
        final ComputerVision computerVision = ComputerVision.getInstance();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                computerVision.stop();
            }
        });

        SantaHatter santaHatter = new SantaHatter(System.getProperty("faceDetectionConfigurationFilePath"));
        santaHatter.addListener(new SantaHatter.Listener() {
            public long lastUpdate = System.currentTimeMillis();
            public boolean currentFaceHasBeenSaved;

            @Override
            public void onFaceDetected() {
            }

            @Override
            public void onStaticFaceDetected() {
            }

            @Override
            public void onPostProcessed(SantaHatter.FaceStatus faceStatus, BufferedImage image, long smallFaceMovementsSince) {
                if (faceStatus == SantaHatter.FaceStatus.NO) {
                    frame.updateView(image, -1, "Hi there.", "Too shy to come", "up to the camera?");
                    currentFaceHasBeenSaved = false;
                } else {
                    long timeFaceHasBeenStill = System.currentTimeMillis() - smallFaceMovementsSince;
                    double waitProgress = 1.0 * timeFaceHasBeenStill / 3000;
                    if (waitProgress > 1.0) {
                        // Wait is over. Face has been still for enough time.
                        frame.updateView(image, -1, "*click*", "Thank you.");
                        if (!currentFaceHasBeenSaved) {

                            try (
                                    Socket socket = new Socket(Util.getServerHost(), Util.getServerPort());
                                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
                            ) {
                                ImageIO.write(image, "png", out);
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } finally {

                            }
                        }
                        currentFaceHasBeenSaved = true;
                    } else {
                        if (waitProgress < 0.1) {
                            frame.updateView(image, -1, "Hold your", "head still", "for a moment.");
                        } else {
                            frame.updateView(image, waitProgress, "Almost", "there...");
                        }
                        currentFaceHasBeenSaved = false;
                    }
                }
            }
        });
        computerVision.addImageAnalyser(santaHatter);
        computerVision.start();
    }
}
