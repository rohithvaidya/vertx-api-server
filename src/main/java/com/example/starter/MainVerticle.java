package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions; // For PostgreSQL connection options
import io.vertx.sqlclient.PoolOptions; // For configuring the connection pool
import io.vertx.sqlclient.SqlClient; // For the client interface
import io.vertx.sqlclient.RowSet; // To handle the result set
import io.vertx.sqlclient.Row; // To represent rows in the result set
import io.vertx.pgclient.PgBuilder; // To build the PostgreSQL client
import io.vertx.sqlclient.Pool;

import java.util.*;  
import java.util.Base64;

public class MainVerticle extends AbstractVerticle {

  String username;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    //Using a post request to print the json request body
    router.post("/post-endpoint").handler(ctx -> {

      PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(5432)
        .setHost("localhost")
        .setDatabase("a_db")
        .setUser("postgres")
        .setPassword("");

     
      PoolOptions poolOptions = new PoolOptions()
        .setMaxSize(5);

     
      Pool client = Pool.pool(vertx, connectOptions, poolOptions);

    
      client
        .query("SELECT * FROM a")
        .execute()
        .onComplete(ar -> {
          if (ar.succeeded()) {
            RowSet<Row> result = ar.result();
            for (Row row : result) {
        // Access column values by name
        String column1 = row.getString("name");
        Integer column2 = row.getInteger("id");

        // Print row data
        System.out.println("id " + column2 + ", name " + column1);
      }
          } else {
            System.out.println("Failure: " + ar.cause().getMessage());
          }

          // Now close the pool
          client.close();
        });

      String auth_header = ctx.request().getHeader("Authorization");
      System.out.println(auth_header);
      String base64Credentials = auth_header.substring("Basic ".length()).trim();
      System.out.println(base64Credentials);
      String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
      System.out.println(credentials);

      String username = credentials.split(":")[0];

      
      ctx.request().bodyHandler(body -> {
        String requestBody = body.toString();
        ctx.response()
          .putHeader("content-type", "application/json")
          .end("Hi "+username + " " + requestBody);
      });
    });

    router.get("/get-endpoint").handler(ctx -> {

      if(ctx.request().getHeader("Authorization") != null){
        String auth_header = ctx.request().getHeader("Authorization");
        System.out.println(auth_header);
        String base64Credentials = auth_header.substring("Basic ".length()).trim();
        System.out.println(base64Credentials);
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        System.out.println(credentials);
        username = credentials.split(":")[0];
      }
    
      ctx.response()
        .putHeader("content-type", "text/plain")
        .end("Hi " + username + " GET request successful!");

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
