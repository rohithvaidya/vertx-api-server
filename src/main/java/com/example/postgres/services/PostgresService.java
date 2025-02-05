package com.example.postgres.services;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import com.example.common.models.Query;

@VertxGen
@ProxyGen
public interface PostgresService {
    
    Future<JsonObject> executeQuery(Query query);
    
    
    Future<Void> executeUpdate(Query query);

    static PostgresService createProxy(Vertx vertx, String address) {
        return new PostgresServiceVertxEBProxy(vertx, address);
    }
}
