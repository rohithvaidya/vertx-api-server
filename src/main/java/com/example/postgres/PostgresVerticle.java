package com.example.postgres;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import com.example.postgres.services.PostgresService;
import com.example.postgres.services.PostgresServiceImpl;

import io.vertx.serviceproxy.ServiceBinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresVerticle extends AbstractVerticle {
    private final static Logger LOGGER = LogManager.getLogger(PostgresVerticle.class);
    private MessageConsumer<JsonObject> consumer;
    private ServiceBinder binder;

    @Override
    public void start(Promise<Void> startPromise) {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:postgresql://postgres:5432/mydb")
                .put("driver_class", "org.postgresql.Driver")
                .put("user", "user")
                .put("password", "password");

        PostgresService service = new PostgresServiceImpl(vertx, config);
        binder = new ServiceBinder(vertx);
        consumer = binder.setAddress("postgres.service").register(PostgresService.class, service);
        LOGGER.info("Postgres verticle started.");
        startPromise.complete();
    }
    @Override
    public void stop() {
        binder.unregister(consumer);
    }
}