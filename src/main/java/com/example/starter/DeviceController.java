package com.example.starter;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(Vertx vertx) {
        this.deviceService = new DeviceServiceVertxEBProxy(vertx, "services.address");
    }

    public void getAllDevices(RoutingContext ctx) {
      deviceService.getAllDevices()
            .onSuccess(devices -> {
                ctx.response()
                    .putHeader("content-type", "application/json")
                    .end(new JsonObject().put("devices", devices).encode());
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", err.getMessage()).encode());
            });
    }

    public void addDevice(RoutingContext ctx) {
        JsonObject device = ctx.getBodyAsJson();
        if (device == null) {
            ctx.response()
                .setStatusCode(400)
                .end(new JsonObject().put("error", "Invalid JSON body").encode());
            return;
        }

      deviceService.addDevice(device)
            .onSuccess(v -> {
                ctx.response()
                    .setStatusCode(201)
                    .end(new JsonObject().put("message", "Device added successfully").encode());
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Failed to add device").encode());
            });
    }

    public void updateDevice(RoutingContext ctx) {
        String deviceId = ctx.pathParam("id");
        JsonObject device = ctx.getBodyAsJson();
        if (device == null) {
            ctx.response()
                .setStatusCode(400)
                .end(new JsonObject().put("error", "Invalid JSON body").encode());
            return;
        }

      deviceService.updateDevice(deviceId, device)
            .onSuccess(v -> {
                ctx.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("message", "Device updated successfully").encode());
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Failed to update device").encode());
            });
    }

    public void deleteDevice(RoutingContext ctx) {
        String deviceId = ctx.pathParam("id");
      deviceService.deleteDevice(deviceId)
            .onSuccess(v -> {
                ctx.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("message", "Device deleted successfully").encode());
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Failed to delete device").encode());
            });
    }
}
