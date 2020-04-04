package kafka.study.requester.channel.http

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import kafka.study.requester.AdvertisementRepository
import kafka.study.requester.domain.AdvertisementRequest
import kafka.study.requester.infra.SystemData

class AdvertisementHTTPRequester(private val config: SystemData, private val repo: AdvertisementRepository) : AbstractVerticle() {

  override fun start() {
    val webClient = WebClient.create(this.vertx)
    vertx.eventBus().consumer<String>("new-adv-request") {
      val adv = Json.decodeValue(it.body(), AdvertisementRequest::class.java)
      webClient.postAbs(this.config.gatewayHost).sendJsonObject(JsonObject(Json.encode(adv))) {
        if (201 == it.result().statusCode()){
          val opportunityId = it.result().headers()["X-opportunity-id"]
          this.repo.add(opportunityId,adv)
        }
      }
    }
  }
}
