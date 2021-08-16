import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertx -> {
                    vertx.deployVerticle("HeatSensor", new DeploymentOptions().setInstances(4));
                    vertx.deployVerticle("SensorData");
                    vertx.deployVerticle("Listener");
                    vertx.deployVerticle("HttpServer");
                });
    }
}
