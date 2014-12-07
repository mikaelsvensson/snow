package snow.photobooth;

import snow.Util;
import snow.computervision.CameraRotator;
import snow.computervision.ComputerVision;
import snow.computervision.Rotation;
import snow.computervision.SantaHatter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class PhotoboothApplication implements Runnable {

    private static final int HOLD_TIME = 2000;
    private final Rectangle startUpWindowBound;
    private final Rotation rotation;

    public PhotoboothApplication(Rectangle startUpWindowBound, Rotation rotation) {
        this.startUpWindowBound = startUpWindowBound;
        this.rotation = rotation;
    }

    public static void main(String[] args) {
        Rotation rotation = null;
        try {
            rotation = Rotation.valueOf(args[0].toUpperCase());
            args = Arrays.copyOfRange(args, 1, args.length);
        } catch (IllegalArgumentException e) {
        }
        final Rectangle[] configurations = Util.getWindowBounds(args);

        SwingUtilities.invokeLater(new PhotoboothApplication(configurations[0], rotation));
    }

    @Override
    public void run() {
        final PhotoboothFrame frame = new PhotoboothFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setBounds(startUpWindowBound);
        frame.setVisible(true);
        final ComputerVision computerVision = ComputerVision.getInstance();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                computerVision.stop();
            }
        });
        frame.setMessageVisible(!Boolean.valueOf(System.getProperty("hideMessage", Boolean.FALSE.toString())));

        SantaHatter santaHatter = new SantaHatter(System.getProperty("faceDetectionConfigurationFilePath"), false);
        santaHatter.addListener(new SantaHatter.Listener() {
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
                    frame.updateView(image, -1, "Kom närmare!", "Var inte blyg.");
                    currentFaceHasBeenSaved = false;
                } else {
                    long timeFaceHasBeenStill = System.currentTimeMillis() - smallFaceMovementsSince;
                    double waitProgress = 1.0 * timeFaceHasBeenStill / HOLD_TIME;
                    if (waitProgress > 1.0) {
                        // Wait is over. Face has been still for enough time.
                        frame.updateView(image, -1, "*KLICK*", "Tack.");
                        if (!currentFaceHasBeenSaved) {

                            try (
                                    Socket socket = new Socket(Util.getServerHost(), Util.getServerPort());
                                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
                            ) {
                                frame.updateView(image, -1, "Bilden skickas", "till skärmen");
                                ImageIO.write(image, "jpg", out);
                                frame.updateView(image, -1, "Klart :-)");
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } finally {

                            }
                        }
                        currentFaceHasBeenSaved = true;
                    } else {
                        if (waitProgress < 0.1) {
                            frame.updateView(image, -1, "Håll huvudet", "stilla en", "liten stund.");
                        } else {
                            frame.updateView(image, waitProgress, "Nästan", "klart...");
                        }
                        currentFaceHasBeenSaved = false;
                    }
                }
            }
        });
        if (rotation != null) {
            computerVision.addImageAnalyser(new CameraRotator(rotation));
        }
        computerVision.addImageAnalyser(santaHatter);
        computerVision.start();
    }

}
