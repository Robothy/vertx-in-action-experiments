import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.Random;
import java.util.UUID;

public class HeatSensor extends AbstractVerticle {

    private double temperature = 27;

    private Random random = new Random();

    private String sensorId = UUID.randomUUID().toString();

    @Override
    public void start() {
        vertx.setPeriodic(3000, this::scheduleNextUpdate);
    }

    private void scheduleNextUpdate(long id) {
        vertx.setTimer(5000 + random.nextInt(3000), this::updateTemperature);
    }

    private void updateTemperature(long id) {
        this.temperature += random.nextGaussian();
        var payload = new JsonObject();
        payload.put("id", sensorId);
        payload.put("temp", temperature);
        vertx.eventBus().publish("sensor.updates", payload);
        scheduleNextUpdate(id);
    }
}
