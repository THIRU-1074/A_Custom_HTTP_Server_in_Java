package customHttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Jolt {

    Request req;
    Response res;
    Socket socket;
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

    void listen(int port, Runnable callBack) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            callBack.run();
            try {
                while (true) {
                    socket = serverSocket.accept();
                    socket.setSoTimeout(5000);
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
                            if (getHandlers.get(req.url) != null) {
                                res.statusCode = 200;
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
                            }
                            while (i == callBackLen) {
                                postHandlers.get(req.url).get(i).run();
                                i++;
                            }
                        }
                    }
                    if (!respond()) {
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Read TimeOut....Closing Client Socket");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean respond() {
        try {
            res.serialize();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(res.serialized.getBytes());
            if (res.body instanceof image) {
                image img = (image) res.body;
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
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
