import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(AbstractVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer("sensor.updates", this::record);
    }

    private void record(Message<JsonObject> msg) {
        var body = msg.body();
        logger.info("Sensor: {}, temperature: {}", body.getString("id"), String.format("%.2f", body.getDouble("temp")));
    }
}
