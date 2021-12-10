import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class HttpServer {
    public static final String BOUNDARY = "b0oundary";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer server = vertx.createHttpServer().requestHandler(
                HttpServer::handleRequest
        );

        server.listen(8877, "localhost").onComplete(
                res -> {
                    if (res.failed()) {
                        System.out.println("Failed to start");
                        res.cause().printStackTrace();
                    } else {
                        System.out.println("Started at " + server.actualPort());
                    }
                }
        );
    }

    //    mstodo  change to returning a multipart response
    private static void handleRequest(HttpServerRequest request) {
        MultiMap params = request.params();
        String amountSize = params.get("amount");

        int amount = amountSize == null ? 10 : Integer.parseInt(amountSize);

        HttpServerResponse resp = request.response();
        resp.setChunked(true);
        resp.putHeader("Content-Type", "multipart/mixed; boundary=" + BOUNDARY);
        resp.write("This is a message with multiple parts in MIME format.\r\n");
        resp.write("--" + BOUNDARY + "\r\n");

        resp.write("Content-Type: text/plain\r\n" +
                "\r\n" +
                "This is the body of the message.\r\n");

        resp.write("--" + BOUNDARY + "\r\n");
        for (int i = 0; i < amount; i++) {
            resp.write("some line of text that will get\r\n" +
                    "followed by another line\r\n");
        }
        resp.write("--" + BOUNDARY + "--\r\n");
        resp.end();
    }
}
