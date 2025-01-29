package com.example.starter;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceModel {

    private final Pool client;

    public DeviceModel(Vertx vertx) {
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("postgres")
            .setUser("postgres")
            .setPassword("");

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        this.client = Pool.pool(vertx, connectOptions, poolOptions);
    }

    public Future<List<Map<String, Object>>> getAllDevices() {
        return client.query("SELECT * FROM device")
            .execute()
            .map(rows -> {
                List<Map<String, Object>> devices = new ArrayList<>();
                for (Row row : rows) {
                    Map<String, Object> device = Map.of(
                        "deviceID", row.getValue(0),
                        "Domain", row.getValue(1),
                        "state", row.getValue(2),
                        "city", row.getValue(3),
                        "location", row.getValue(4),
                        "deviceType", row.getValue(5)
                    );
                    devices.add(device);
                }
                return devices;
            });
    }

    public Future<Void> addDevice(JsonObject device) {
        String query = "INSERT INTO device (deviceId, Domain, state, city, location, deviceType) " +
                       "VALUES ($1, $2, $3, $4, $5, $6)";

        JsonObject location = device.getJsonObject("location");
        String locationString = location != null ? location.encode() : null;

        return client.preparedQuery(query)
            .execute(Tuple.of(
                device.getString("deviceId"),
                device.getString("Domain"),
                device.getString("state"),
                device.getString("city"),
                locationString,
                device.getString("deviceType")
            ))
            .mapEmpty();
    }

    public Future<Void> updateDevice(String deviceId, JsonObject device) {
        String query = "UPDATE device SET " +
                       "Domain = $1, " +
                       "state = $2, " +
                       "city = $3, " +
                       "location = $4, " +
                       "deviceType = $5 " +
                       "WHERE deviceId = $6";

        JsonObject location = device.getJsonObject("location");
        String locationString = location != null ? location.encode() : null;

        return client.preparedQuery(query)
            .execute(Tuple.of(
                device.getString("Domain"),
                device.getString("state"),
                device.getString("city"),
                locationString,
                device.getString("deviceType"),
                deviceId
            ))
            .mapEmpty();
    }

    public Future<Void> deleteDevice(String deviceId) {
        String query = "DELETE FROM device WHERE deviceId = $1";
        return client.preparedQuery(query)
            .execute(Tuple.of(deviceId))
            .mapEmpty();
    }

    public void close() {
        client.close();
    }
}