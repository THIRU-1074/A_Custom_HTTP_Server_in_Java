package customHttpServer;

import java.nio.charset.StandardCharsets;
import java.util.*;

class Response {

    String serialized;
    Map<String, String> headers;
    Content body;

    void serialize() {
        //String b = "<html><body>Hello, World!</body></html>";
        serialized
                = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + body.serialized.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body.serialized;
    }
}
