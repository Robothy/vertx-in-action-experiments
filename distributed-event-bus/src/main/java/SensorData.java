import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SensorData extends AbstractVerticle {

    private final Map<String, Double> lastValues = new HashMap<>();

    @Override
    public void start() {
        var bus = vertx.eventBus();
        bus.consumer("sensor.updates", this::update);
        bus.consumer("sensor.average", this::average);
    }

    private void update(Message<JsonObject> msg) {
        var body = msg.body();
        lastValues.put(body.getString("id"), body.getDouble("temp"));
    }

    private void average(Message<JsonObject> msg) {
        double avg = lastValues.values()
                .stream()
                .collect(Collectors.averagingDouble(Double::doubleValue));
        var body = new JsonObject()
                .put("average", avg);
        msg.reply(body);
    }
}
