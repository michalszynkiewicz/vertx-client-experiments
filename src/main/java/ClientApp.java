import com.github.michalszynkiewicz.kmp.BoundaryFinder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpHeaders.Values.BOUNDARY;

public class ClientApp {

    private static final File downloads = new File("/tmp/rest-client-downloads");
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String BOUNDARY_PARAM = ';' + BOUNDARY + '=';

    static Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {

        vertx.createHttpClient()
                .request(HttpMethod.GET, 8080, "localhost", "/hello", ClientApp::handleRequestCreated);
    }

    private static void handleRequestCreated(AsyncResult<HttpClientRequest> result) {
        if (result.failed()) {
            System.out.println("Failed to create request");
            result.cause().printStackTrace();
        } else {
            HttpClientRequest request = result.result();
            request.send().onComplete(ClientApp::handleResponse);
        }
    }

    private static void handleResponse(AsyncResult<HttpClientResponse> r) {
        if (r.failed()) {
            System.out.println("Failed to get response");
            r.cause().printStackTrace();
        } else {
            HttpClientResponse response = r.result();

            String contentType = response.getHeader(CONTENT_TYPE);
            System.out.println("Content-Type: " + contentType);
            int splitterPosition = contentType.indexOf(';');

            if (splitterPosition > -1) {
                String originalType = contentType;
                contentType = originalType.substring(0, splitterPosition);
                // mstodo support custom part of the content type
                if (contentType.equalsIgnoreCase("multipart/form-data")){
// mstodo let's make params start with ';'
                    String params = originalType.substring(splitterPosition);
                    int boundaryStart = params.indexOf(BOUNDARY_PARAM);
                    if (boundaryStart < 0) {
                        throw new IllegalArgumentException("Invalid multipart params, cannot find boundary");
                    }
                    boundaryStart += BOUNDARY_PARAM.length();

                    if (boundaryStart >= params.length()) {
                        throw new IllegalArgumentException("Empty boundary");
                    }

                    // mstodo test for empty boundary
                    int boundaryEnd = params.indexOf(';', boundaryStart + 1);
                    boundaryEnd = boundaryEnd < 0 ? params.length() : boundaryEnd;
                    String boundary = "--" + params.substring(boundaryStart, boundaryEnd);
                    System.out.println("Boundary " + boundary);

                    MultipartResponseParser parser = new MultipartResponseParser(boundary);
                    response.handler(parser);

                    response.endHandler(v -> {
                        System.out.println("done");
                        vertx.close();
                    });
                }
            }
//            response.fetch(10);
        }
    }

    static void print(Buffer buffer) {
        System.out.println("chunk: " + buffer.toString());
    }

    public static class MultipartResponseParser implements Handler<Buffer> {
        private final BoundaryFinder boundaryFinder;
        final Map<String, Buffer> attributes = new HashMap<>();
        final Map<String, FileAttribute> files = new HashMap<>();

        public MultipartResponseParser(String boundary) {
            boundaryFinder = new BoundaryFinder(boundary);
        }

        @Override
        public void handle(Buffer event) {
                File file = new File(downloads, UUID.randomUUID().toString());
                for (byte aByte : event.getBytes()) {
                    // mstodo if octet stream - decode!
                    // mstodo maybe just find the netty classes that do the parsing for the server side?
                    boundaryFinder.addAndCheck()
                }
        }
    }

    public static class FileAttribute {
        final File file;
        final String fileName;
        // mstodo check:

        // HttpPostRequestDecoder

        public FileAttribute(File file, String fileName) {
            this.file = file;
            this.fileName = fileName;
        }
    }
}
