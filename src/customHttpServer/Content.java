package customHttpServer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

abstract class Content {

    String serialized;
    int contentLength;
    String contentType;

    abstract void serialize();
}

class JSON extends Content {

    JSONObject json = new JSONObject();

    void add(String key, String value) {
        json.put(key, value);
    }

    @Override
    void serialize() {
        serialized = json.toJSONString();
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
    String format;

    image(String path) {
        this.path = path;
        format = path.substring(path.lastIndexOf('.') + 1);
    }
    byte[] imageSerial;

    @Override
    void serialize() {
        BufferedImage bImage;
        try {
            bImage = ImageIO.read(new File(path));
            // Serialize to file
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, format, bos);
            imageSerial = bos.toByteArray();
            System.out.println(imageSerial.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
