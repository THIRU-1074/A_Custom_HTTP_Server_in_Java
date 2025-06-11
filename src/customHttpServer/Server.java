package customHttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {

    public static void main(String[] args) {
        int port = 5000; // you can choose any open port

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept(); // Accept client connection
                System.out.println("New client connected");

                // Read data from client
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int contentLength = 0;

// Read headers
                String line;
                line = reader.readLine();
                if (line == null || line.length() == 0) {
                    socket.close();
                    continue;
                }
                Request req = new Request();
                System.out.println(line);
                req.method = line.split(" ")[0];
                req.url = line.split(" ")[1];
                req.version = line.split(" ")[2];
                boolean flag = false;
                while ((line = reader.readLine()) != null && !(line.isEmpty())) {
                    flag = true;
                    System.out.println("Received: " + line);
                    req.headers.put(line.split(": ")[0], line.split(": ")[1]);
                }

// Read body
                if (req.method.equals("POST") || req.method.equals("PUT")) {
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
                }
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                if (req.body != null) {
                    req.body.serialized = new String(body);
                }
                System.out.println("Received: " + new String(body));
                Response res = new Response();
                res.body = new html(
                        "src/resources/index.html");
                res.body.serialize();
                res.serialize();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true); // autoFlush = true
                if (flag) {
                    writer.print(res.serialized);
                    writer.flush(); // ensure all data is sent
                }
                socket.close();
                System.out.println("Client disconnected");
                for (Map.Entry<String, String> ele : req.headers.entrySet()) {
                    System.out.println(ele.getKey() + ": " + ele.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
