package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.serviceproxy.ServiceBinder;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        DeviceService deviceService = new DeviceModel(vertx);
      new ServiceBinder(vertx)
        .setAddress("service.address")
        .register(DeviceService.class, deviceService);

        DeviceController deviceController = new DeviceController(vertx);

        // Define routes
        router.get("/view-device").handler(deviceController::getAllDevices);
        router.post("/add-device").handler(deviceController::addDevice);
        router.put("/update-device/:id").handler(deviceController::updateDevice);
        router.delete("/delete-device/:id").handler(deviceController::deleteDevice);

        // Start server
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888)
            .onSuccess(http -> {
                System.out.println("HTTP server started on port 8888");
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
}
