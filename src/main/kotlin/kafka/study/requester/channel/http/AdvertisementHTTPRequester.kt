package kafka.study.requester.channel.http

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import kafka.study.requester.AdvertisementRepository
import kafka.study.requester.domain.AdvertisementRequest
import kafka.study.requester.infra.SystemData
import java.util.logging.Level
import java.util.logging.Logger

class AdvertisementHTTPRequester(private val config: SystemData, private val repo: AdvertisementRepository) : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementHTTPRequester")

  override fun start() {
    val webClient = WebClient.create(this.vertx)
    vertx.eventBus().consumer<String>("new-adv-request") {
      val adv = Json.decodeValue(it.body(), AdvertisementRequest::class.java)
      webClient.postAbs(this.config.gatewayHost).putHeader("X-Customer-Key",config.customerKey).sendJsonObject(JsonObject(Json.encode(adv))) { itHttp ->
        LOGGER.info("Advertisement server response. Status code: ${itHttp.result().statusCode()}")
        if(itHttp.failed()){
          LOGGER.severe("Error to create advertisement server error. Status code: ${itHttp.result().statusCode()}")
        }else if (201 == itHttp.result().statusCode()){
          val opportunityId = itHttp.result().headers()["X-opportunity-id"]
          LOGGER.info("Advertisement request created successfully. Opportunity ID: $opportunityId")
          this.repo.add(opportunityId,adv)
        }
      }
    }
  }
}
