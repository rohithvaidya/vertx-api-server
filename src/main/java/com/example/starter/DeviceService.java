package com.example.starter;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map;

import io.vertx.codegen.annotations.ProxyGen;

@VertxGen
@ProxyGen
public interface DeviceService {

  static DeviceService createProxy(Vertx vertx, String address) {
    return new DeviceServiceVertxEBProxy(vertx, address);
  }

    Future<List<String>> getAllDevices();

    Future<Void> addDevice(JsonObject device);

    Future<Void> updateDevice(String deviceId, JsonObject device);

    Future<Void> deleteDevice(String deviceId);


}
