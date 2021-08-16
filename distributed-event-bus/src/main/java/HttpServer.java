import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public class HttpServer extends AbstractVerticle {
    @Override
    public void start() {
        String port = System.getenv("PORT");
        vertx.createHttpServer()
                .requestHandler(this::handler)
                .listen(port == null ? 8080 : Integer.parseInt(port));
    }

    private void handler(HttpServerRequest request) {
        var response = request.response();
        if ("/".equals(request.path())) {
            response.sendFile("index.html");
        } else if ("/sse".equals(request.path())) {
            sse(request);
        } else {
            response.setStatusCode(404);
        }
    }

    private void sse(HttpServerRequest request) {
        var response = request.response();
        var bus = vertx.eventBus();
        response.putHeader("Content-Type", "text/event-stream")
                .putHeader("Cache-Control", "no-cache")
                .setChunked(true);
        var consumer = bus.<JsonObject>consumer("sensor.updates");
        consumer.handler(msg -> {
            response.write("event: update\n");
            response.write("data: " + msg.body().encode() + "\n\n");
        });

        var ticks = vertx.periodicStream(1000);
        ticks.handler(id -> bus.<JsonObject>request("sensor.average", "", reply -> {
            if (reply.succeeded()) {
                response.write("event: average\n");
                response.write("data: " + reply.result().body().encode() + "\n\n");
            }
        }));

        response.endHandler(v -> {
            consumer.unregister();
            ticks.cancel();
        });
    }

}
