package customHttpServer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

abstract class Content {

    String serialized;
    int contentLength;
    String contentType;

    abstract void serialize();
}

class JSON extends Content {

    @Override
    void serialize() {

    }
}

class text extends Content {

    @Override
    void serialize() {

    }
}

class html extends Content {

    String path;

    html(String path) {
        this.path = path;
    }

    @Override
    void serialize() {
        try {
            serialized = Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class image extends Content {

    String path;

    image(String path) {
        this.path = path;
    }
    byte[] serialized;

    void serialize() {
        BufferedImage original;
        try {
            original = ImageIO.read(new File(path));

            // Serialize to file
            serialized = ImageSerializer.imageToBytes(original, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
