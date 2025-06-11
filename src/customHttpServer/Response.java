package customHttpServer;

import java.nio.charset.StandardCharsets;
import java.util.*;

class Response {

    static Map<Integer, String> statusMap;
    String serialized;
    Map<String, String> headers;
    Content body;
    int statusCode;
    String version;

    Response() {
        version = "HTTP/1.1";
        headers = new HashMap<>();
    }

    static void statusMapper() {
        statusMap = Map.ofEntries(
                Map.entry(200, "OK"),
                Map.entry(201, "Created"),
                Map.entry(204, "No Content"),
                Map.entry(400, "Bad Request"),
                Map.entry(401, "Unauthorized"),
                Map.entry(404, "Not Found"),
                Map.entry(500, "Internal Server Error"),
                Map.entry(503, "Service Unavailable")
        );
    }

    void serialize() {
        body.serialize();
        serialized
                = version + " " + String.valueOf(statusCode) + " " + statusMap.get(statusCode) + "\r\n";
        serialized += "Content-Length: " + body.serialized.getBytes(StandardCharsets.UTF_8).length + "\r\n";
        for (Map.Entry<String, String> element : headers.entrySet()) {
            String key = element.getKey();
            String val = element.getValue();
            serialized += key + ": " + val + "\r\n";
        }
        serialized += "\r\n" + body.serialized;
    }
}
