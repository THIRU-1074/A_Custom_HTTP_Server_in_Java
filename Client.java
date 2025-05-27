
import java.io.*;
import java.net.*;

public class Client {

    public void go() {
        try {
            Socket s = new Socket("192.168.1.198", 4242);
            InputStreamReader streamReader = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            String msg = reader.readLine();
            System.out.println(msg);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.go();
    }
}
