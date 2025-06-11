package customHttpServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

abstract class Content {

    String serialized;
    int contentLength;
    String contentType;

    abstract void serialize();
}

class JSON extends Content {

    void serialize() {

    }
}

class text extends Content {

    void serialize() {

    }
}

class html extends Content {

    String path;

    html(String path) {
        this.path = path;
    }

    void serialize() {
        try {
            serialized = Files.readString(Paths.get(path)); // Java 11+

            //System.out.println(html); // or send over socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
