package customHttpServer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Server {

    public static void main(String[] args) {
        Jolt app = new Jolt();
        app.GET("/", () -> {
            app.res.body = new html("src/resources/index.html");
        });
        app.GET("/favicon.ico", () -> {
            app.res.body = new image("src/resources/image.png");
        });
        app.GET("/getClassesInfo", () -> {
            app.res.body = new JSON();
            JSON localObJson = (JSON) app.res.body;
            localObJson.add("Name", "RegNo");
            localObJson.add("Thiru", "22BEC1473");
        });
        app.POST("/login", () -> {
            JSON localObJson = (JSON) app.req.body;
            String userName = (String) localObJson.get("user_name");
            String password = (String) localObJson.get("password");
            System.out.println(userName + password);
        });
        app.GET("/login", () -> {
            app.res.body = new html("src/resources/login.html");
        });
        app.listen(5000, () -> {
            System.out.println("The Server is Running...");
        });

    }
}
