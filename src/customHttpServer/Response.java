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
        if (body != null) {
            body.serialize();
            switch (body) {
                case image img -> {
                    headers.put("Content-Type", "image/" + img.format);
                    break;
                }
                case JSON json -> {
                    headers.put("Content-Type", "application/json");
                }
                case html html -> {
                    headers.put("Content-Type", "text/html");
                }
                case text txt -> {
                    headers.put("Content-Type", "text/plain");
                }
                default -> {

                }
            }
            if (body instanceof image) {
                image img = (image) body;
                headers.put("Content-Length", String.valueOf(img.imageSerial.length));
            } else {
                headers.put("Content-Length", String.valueOf(body.serialized.getBytes(StandardCharsets.UTF_8).length));
            }
        } else {
            headers.put("Content-Length", "0");
        }
        serialized
                = version + " " + String.valueOf(statusCode) + " " + statusMap.get(statusCode) + "\r\n";
        for (Map.Entry<String, String> element : headers.entrySet()) {
            String key = element.getKey();
            String val = element.getValue();
            serialized += key + ": " + val + "\r\n";
        }
        serialized += "\r\n";
    }
}
