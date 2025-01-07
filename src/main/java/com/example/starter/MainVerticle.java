package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import java.util.*;  

import java.util.Base64;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);

    //Using a post request to print the json request body
    router.post("/post-endpoint").handler(ctx -> {
      
      ctx.request().bodyHandler(body -> {
        String requestBody = body.toString();
        ctx.response()
          .putHeader("content-type", "application/json")
          .end(requestBody);
      });
    });

    router.get("/get-endpoint").handler(ctx -> {
      ctx.response()
        .putHeader("content-type", "text/plain")
        .end("GET request successful!");
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
