package com.example.postgres.services;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import com.example.common.models.Query;
import com.example.common.models.QueryResult;
import io.vertx.core.Promise;
import io.vertx.ext.sql.SQLConnection;

public class PostgresServiceImpl implements PostgresService {

    private final JDBCClient client;

    public PostgresServiceImpl(Vertx vertx, JsonObject config) {
        this.client = JDBCClient.createShared(vertx, config);
    }

    @Override
    public Future<JsonObject> executeQuery(Query query) {
        Promise<JsonObject> promise = Promise.promise();
        client.getConnection(ar -> {
            if (ar.succeeded()) {
                SQLConnection connection = ar.result();
                connection.queryWithParams(query.getSql(), query.getParams(), res -> {
                    if (res.succeeded()) {
                        promise.complete(new QueryResult(res.result().getRows()).toJson());
                    } else {
                        promise.fail(res.cause());
                    }
                    connection.close();
                });
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> executeUpdate(Query query) {
        Promise<Void> promise = Promise.promise();
        client.getConnection(ar -> {
            if (ar.succeeded()) {
                SQLConnection connection = ar.result();
                connection.updateWithParams(query.getSql(), query.getParams(), res -> {
                    if (res.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(res.cause());
                    }
                    connection.close();
                });
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }
}
