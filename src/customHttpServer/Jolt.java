package customHttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Jolt {

    Request req;
    Response res;
    Socket socket;
    Map<String, Runnable> getHandlers;
    Map<String, Runnable> postHandlers;

    Jolt() {
        Response.statusMapper();
        getHandlers = new HashMap<>();
        postHandlers = new HashMap<>();
        req = new Request();
        res = new Response();
    }

    void GET(String url, Runnable callBack) {
        res.headers.put("Connection", "close");
        getHandlers.put(url, callBack);
    }

    void POST(String url, Runnable callBack) {
        postHandlers.put(url, callBack);
    }

    void listen(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                socket = serverSocket.accept();
                System.out.println("New client connected");

// Read data from client
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                line = reader.readLine();
                if (line == null || line.length() == 0) {
                    socket.close();
                    continue;
                }

// Start Line
                System.out.println(line);
                req.method = line.split(" ")[0];
                req.url = line.split(" ")[1];
                req.version = line.split(" ")[2];
// Read Header
                while ((line = reader.readLine()) != null && !(line.isEmpty())) {
                    System.out.println(line);
                    req.headers.put(line.split(": ")[0], line.split(": ")[1]);
                }
// Read body
                if (req.headers.get("Content-Length") != null) {
                    switch (req.headers.get("Content-Type")) {
                        case ("JSON") -> {
                            req.body = new JSON();
                        }
                        default -> {
                            req.body = new text();
                        }
                    }
                    req.body.contentLength = Integer.parseInt(req.headers.get("Content-Length"));
                    req.body.contentType = req.headers.get("Content-Type");
                    char[] body = new char[req.body.contentLength];
                    reader.read(body, 0, req.body.contentLength);
                    req.body.serialized = new String(body);
                    System.out.println("Received: " + new String(body));
                }
                switch (req.method) {
                    case ("GET") -> {
                        getHandlers.get(req.url).run();
                        break;
                    }
                    case ("POST") -> {
                        postHandlers.get(req.url).run();
                    }
                }
                respond();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void respond() {
        try {
            res.serialize();
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true); // autoFlush = true
            writer.print(res.serialized);
            writer.flush(); // ensure all data is sent
            socket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
