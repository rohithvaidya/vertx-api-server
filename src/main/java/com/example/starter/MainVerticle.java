package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions; 
import io.vertx.sqlclient.PoolOptions; 
import io.vertx.sqlclient.SqlClient; 
import io.vertx.sqlclient.RowSet; 
import io.vertx.sqlclient.Row; 
import io.vertx.pgclient.PgBuilder; 
import io.vertx.sqlclient.Pool;
import com.google.gson.Gson;
import com.google.gson.JsonParser;


import java.util.*;  
import java.util.Base64;

import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Row;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  String username;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);
    

    router.get("/view-device").handler(ctx -> {

      if(ctx.request().getHeader("Authorization") != null){
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
      }
      else{
        ctx.response()
        .setStatusCode(401)
        .putHeader("content-type", "application/json")
        .end("{\"error\":\"No Auth\"}");
      }

      PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("");

    
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    
    Pool client = Pool.pool(vertx, connectOptions, poolOptions);

      List<Map<String, Object>> rowsList = new ArrayList<>();

      client
        .query("SELECT * FROM device")
        .execute()
        .onComplete(ar -> {
          if (ar.succeeded()) {
            RowSet<Row> result = ar.result();
            for (Row row : result) {
              Map<String, Object> response_json = new HashMap<>();
              
              response_json.put("deviceID", row.getValue(0));
              response_json.put("Domain", row.getValue(1));
              response_json.put("state", row.getValue(2));
              response_json.put("city", row.getValue(3));
              response_json.put("location", row.getValue(4));
              response_json.put("deviceType", row.getValue(5));
              System.out.println(response_json);
              rowsList.add(response_json);

      }

      Gson gson = new Gson();
      String json = gson.toJson(rowsList);
  
      ctx.response()
        .putHeader("content-type", "application/json")
        .end(json);

          } else {
            System.out.println("Failure: " + ar.cause().getMessage());
          }
          client.close();
        });

    });

    router.post("/add-device").handler(ctx -> {

      if(ctx.request().getHeader("Authorization") != null){
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
      }
      else{
        ctx.response()
        .setStatusCode(401)
        .putHeader("content-type", "application/json")
        .end("{\"error\":\"No Auth\"}");
      }

      PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("");

    
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    
    Pool client = Pool.pool(vertx, connectOptions, poolOptions);

    
    Promise<Void> dbOperationPromise = Promise.promise();

    
    ctx.request().bodyHandler(body -> {
      String requestBody = body.toString();
      JsonObject rbody = new JsonObject(requestBody);
      if (rbody == null) {
      ctx.response()
        .setStatusCode(400)
        .putHeader("content-type", "application/json")
        .end("{\"error\": \"Invalid JSON body\"}");
      return;
      }

      

      String query = "INSERT INTO device (deviceId, Domain, state, city, location, deviceType) " +
                     "VALUES ($1, $2, $3, $4, $5, $6)";

      JsonObject location = rbody.getJsonObject("location");
      String locationString = location != null ? location.encode() : null;

      System.out.println(rbody);
      System.out.println(locationString);

      client.preparedQuery(query)
        .execute(Tuple.of(
          rbody.getString("deviceId"),
          rbody.getString("Domain"),
          rbody.getString("state"),
          rbody.getString("city"),
          locationString,
          rbody.getString("deviceType")))
        .onSuccess(res -> {
          
          dbOperationPromise.complete();
        })
        .onFailure(err -> {
          
          dbOperationPromise.fail(err);
        });
      
      
      dbOperationPromise.future().onComplete(promiseResult -> {
            if (promiseResult.succeeded()) {
                client.close();
                ctx.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json")
                    .end("{\"message\": \"Device added successfully\"}");
            } else {
                client.close();
                ctx.response()
                    .setStatusCode(500)
                    .putHeader("content-type", "application/json")
                    .end("{\"error\": \"Failed to add device\"}");
            }
      
      
      });

    });
    });


    router.post("/async-add-device").handler(ctx -> {

      if(ctx.request().getHeader("Authorization") != null){
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
      }
      else{
        ctx.response()
        .setStatusCode(401)
        .putHeader("content-type", "application/json")
        .end("{\"error\":\"No Auth\"}");
      }

      ctx.request().bodyHandler(body -> {
      String requestBody = body.toString();
      JsonObject rbody = new JsonObject(requestBody);
      if (rbody == null) {
      ctx.response()
        .setStatusCode(400)
        .putHeader("content-type", "application/json")
        .end("{\"error\": \"Invalid JSON body\"}");
      return;
      }
      

      ctx.response()
                    .putHeader("content-type", "text/plain")
                    .end("Request received! View device in 2 min");

      Promise<Void> dbOperationPromise = Promise.promise();

      PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("");

    
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    
    Pool client = Pool.pool(vertx, connectOptions, poolOptions);

      String query = "INSERT INTO device (deviceId, Domain, state, city, location, deviceType) " +
                     "VALUES ($1, $2, $3, $4, $5, $6)";

      JsonObject location = rbody.getJsonObject("location");
      String locationString = location != null ? location.encode() : null;

      System.out.println(rbody);
      System.out.println(locationString);

      
      
      vertx.setTimer(2 * 60 * 1000, id -> {
                System.out.println("2 min processing time completed!");
                client.preparedQuery(query)
        .execute(Tuple.of(
          rbody.getString("deviceId"),
          rbody.getString("Domain"),
          rbody.getString("state"),
          rbody.getString("city"),
          locationString,
          rbody.getString("deviceType")))
        .onSuccess(res -> {
        
          dbOperationPromise.complete();
        })
        .onFailure(err -> {
          dbOperationPromise.fail(err);
        });
            });
      
      dbOperationPromise.future().onComplete(promiseResult -> {
            if (promiseResult.succeeded()) {
                client.close();
                System.out.println("User device added successfully!");
            } else {
                client.close();
                System.out.println("Add user device failed!");
            }
    
      });

    });
    });



    router.put("/update-device/:id").handler(ctx -> {
    if (ctx.request().getHeader("Authorization") != null) {
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
    } else {
        ctx.response()
           .setStatusCode(401)
           .putHeader("content-type", "application/json")
           .end("{\"error\":\"No Auth\"}");
        return;
    }

  
    String deviceId = ctx.pathParam("id");

    PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(5432)
        .setHost("localhost")
        .setDatabase("postgres")
        .setUser("postgres")
        .setPassword("");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    Pool client = Pool.pool(vertx, connectOptions, poolOptions);

    // Read the request body
    ctx.request().bodyHandler(body -> {
        String requestBody = body.toString();
        JsonObject rbody = new JsonObject(requestBody);
        if (rbody == null) {
            ctx.response()
               .setStatusCode(400)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Invalid JSON body\"}");
            return;
        }

        // Update query
        String query = "UPDATE device SET " +
                       "Domain = $1, " +
                       "state = $2, " +
                       "city = $3, " +
                       "location = $4, " +
                       "deviceType = $5 " +
                       "WHERE deviceId = $6";

        JsonObject location = rbody.getJsonObject("location");
        String locationString = location != null ? location.encode() : null;

        System.out.println(rbody);
        System.out.println(locationString);

       
        client.preparedQuery(query)
            .execute(Tuple.of(
                rbody.getString("Domain"),
                rbody.getString("state"),
                rbody.getString("city"),
                locationString,
                rbody.getString("deviceType"),
                deviceId))
            .onSuccess(res -> {
                client.close();
                ctx.response()
                   .setStatusCode(200)
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Device updated successfully\"}");
            })
            .onFailure(err -> {
                client.close();
                ctx.response()
                   .setStatusCode(500)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"Failed to update device\"}");
            });
    });

});

router.delete("/delete-device/:id").handler(ctx -> {
    if (ctx.request().getHeader("Authorization") != null) {
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
    } else {
        ctx.response()
           .setStatusCode(401)
           .putHeader("content-type", "application/json")
           .end("{\"error\":\"No Auth\"}");
        return;
    }

    
    String deviceId = ctx.pathParam("id");

    PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(5432)
        .setHost("localhost")
        .setDatabase("postgres")
        .setUser("postgres")
        .setPassword("");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    Pool client = Pool.pool(vertx, connectOptions, poolOptions);

    
    String query = "DELETE FROM device WHERE deviceId = $1";

    
    client.preparedQuery(query)
        .execute(Tuple.of(deviceId))
        .onSuccess(res -> {
            if (res.rowCount() > 0) {
                
                ctx.response()
                   .setStatusCode(200)
                   .putHeader("content-type", "application/json")
                   .end("{\"message\": \"Device deleted successfully\"}");
            } else {
                
                ctx.response()
                   .setStatusCode(404)
                   .putHeader("content-type", "application/json")
                   .end("{\"error\": \"Device not found\"}");
            }
        })
        .onFailure(err -> {
            
            ctx.response()
               .setStatusCode(500)
               .putHeader("content-type", "application/json")
               .end("{\"error\": \"Failed to delete device\"}");
        });
});



    vertx.createHttpServer().requestHandler(router).listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
