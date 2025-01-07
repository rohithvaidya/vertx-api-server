package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

import java.util.*;  
import java.util.Base64;

public class MainVerticle extends AbstractVerticle {

  String username;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    //Using a post request to print the json request body
    router.post("/post-endpoint").handler(ctx -> {

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
