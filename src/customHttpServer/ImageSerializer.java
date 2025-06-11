package customHttpServer;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageSerializer {

    // Convert BufferedImage to byte[]
    public static byte[] imageToBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos); // "png", "jpg", etc.
        return baos.toByteArray();
    }

    // Convert byte[] back to BufferedImage
    public static BufferedImage bytesToImage(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return ImageIO.read(bais);
    }
}
