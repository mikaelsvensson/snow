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
import java.net.UnknownHostException;

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

            @Override
            public void onFaceDetected() {
            }

            @Override
            public void onStaticFaceDetected() {
            }

            @Override
            public void onPostProcessed(SantaHatter.FaceStatus faceStatus, BufferedImage image) {
                if (faceStatus == SantaHatter.FaceStatus.NO) {
                    frame.updateView(image, "Hi there.", "Too shy to come", "up to the camera?");
                } else {
                    if (faceStatus == SantaHatter.FaceStatus.YES_STATIC && System.currentTimeMillis() - lastUpdate > 5000) {
                        frame.updateView(image, "*click*", "Thank you.");

                        try (
                                Socket socket = new Socket(Util.getServerHost(), Util.getServerPort());
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ) {
                            ImageIO.write(image, "png", out);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } finally {

                        }
                        lastUpdate = System.currentTimeMillis();
                    } else {
                        frame.updateView(image, "Hold it...");
                    }
                }
            }
        });
        computerVision.addImageAnalyser(santaHatter);
        computerVision.start();
    }
}
