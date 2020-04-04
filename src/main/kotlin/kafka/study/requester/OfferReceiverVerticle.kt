package kafka.study.requester

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class OfferReceiverVerticle : AbstractVerticle() {

  override fun start() {
    DatabindCodec.mapper().registerModule(KotlinModule())
    var router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.post("/offers").handler { receiveOffer(it,this.vertx) }
    vertx.createHttpServer().requestHandler(router).listen(9999)
  }

  fun receiveOffer(routingContext: RoutingContext, vertx: Vertx) {



  }

  fun HttpServerResponse.error(newStatusCode: Int) = this.setStatusCode(newStatusCode).end()




}

