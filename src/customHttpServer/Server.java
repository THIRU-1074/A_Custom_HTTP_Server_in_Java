package customHttpServer;

public class Server {

    public static void main(String[] args) {
        Jolt app = new Jolt();
        app.GET("/", () -> {
            app.res.headers.put("Content-Type", "html");
            app.res.body = new html("src/resources/index.html");
            app.res.statusCode = 200;
            System.out.println(app.req.url);
        });
        app.GET("/favicon.ico", () -> {
            app.res.headers.put("")
        })
        app.listen(5000);
    }
}
