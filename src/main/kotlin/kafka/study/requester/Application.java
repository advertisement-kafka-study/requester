package kafka.study.requester;

import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(SiteRequester.class.getName());
  }

}
