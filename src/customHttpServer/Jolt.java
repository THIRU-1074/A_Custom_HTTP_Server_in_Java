package customHttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Jolt {

    Request req;
    Response res;
    Map<String, ArrayList<Runnable>> getHandlers;
    Map<String, ArrayList<Runnable>> postHandlers;
    int callBackLen;

    Jolt() {
        Response.statusMapper();
        getHandlers = new HashMap<>();
        postHandlers = new HashMap<>();
        req = new Request();
        res = new Response();
    }

    void next() {
        callBackLen++;
    }

    void GET(String url, Runnable... callBack) {
        res.headers.put("Connection", "close");
        getHandlers.put(url, new ArrayList<>());
        for (Runnable r : callBack) {
            getHandlers.get(url).add(r);
        }
    }

    void POST(String url, Runnable... callBack) {
        postHandlers.put(url, new ArrayList<>());
        for (Runnable r : callBack) {
            postHandlers.get(url).add(r);
        }
    }

    void parseRequest(BufferedReader reader, Socket socket) {
        String line;
        try {
            line = reader.readLine();
            if (line == null || line.length() == 0) {
                socket.close();
                return;
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
                    case ("application/json") -> {
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
                req.body.deserialize();
                System.out.println("Received: " + new String(body));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleRequest() {
        switch (req.method) {
            case ("GET") -> {
                if (getHandlers.get(req.url) != null) {
                    res.statusCode = 200;
                } else {
                    res.statusCode = 404;
                    break;
                }
                int i = 0;
                while (i == callBackLen) {
                    getHandlers.get(req.url).get(i).run();
                    i++;
                }
                break;
            }
            case ("POST") -> {
                int i = 0;
                if (postHandlers.get(req.url) != null) {
                    res.statusCode = 200;
                } else {
                    res.statusCode = 404;
                    break;
                }
                while (i == callBackLen) {
                    postHandlers.get(req.url).get(i).run();
                    i++;
                }
            }
        }
    }

    void handleClient(Socket socket) {
        try {
            try {
                socket.setSoTimeout(120000);
                System.out.println("New client connected");
                while (true) {
                    // Read data from client
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    parseRequest(reader, socket);
                    handleRequest();
                    if (!provideResponse(socket)) {
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Closing Client Socket");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void listen(int port, Runnable callBack) {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                callBack.run();
                new Thread(() -> {
                    try {
                        handleClient(serverSocket.accept());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean provideResponse(Socket socket) {
        try {
            res.serialize();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(res.serialized.getBytes());
            if (res.body == null); else if (res.body instanceof image img) {
                outputStream.write(img.imageSerial);
            } else {
                outputStream.write(res.body.serialized.getBytes());
            }
            outputStream.flush(); // ensure all data is sent
            if (req.headers.get("Connection").equals("keep-alive")) {
                return true;
            }
            socket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
