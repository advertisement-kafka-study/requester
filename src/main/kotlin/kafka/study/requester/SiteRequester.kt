package kafka.study.requester

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.jackson.DatabindCodec
import kafka.study.requester.channel.cloudevents.AdvertisementCloudEventsRequester
import kafka.study.requester.channel.http.AdvertisementHTTPRequester
import kafka.study.requester.infra.SystemData
import kafka.study.requester.producer.AdvertisementRequestProducer
import java.lang.IllegalArgumentException
import java.util.*
import java.util.logging.Logger

class SiteRequester : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("SiteRequester")

  override fun start(startPromise: Promise<Void>) {
    val sysData = SystemData(gatewayHost = System.getenv("GATEWAY_HOST"), customerKey = System.getenv("CUSTOMER_KEY"), callback = System.getenv("MY_HOST"),portType =  System.getenv("PORT_TYPE"),timerDelay = System.getenv("TIMER_DELAY").toLong())
    LOGGER.info("""
      ================================================================
      System configuration:

      GATEWAY_HOST : ${sysData.gatewayHost}
      MY_HOST :       ${sysData.callback}
      CUSTOMER_KEY :       ${sysData.customerKey}
      PORT_TYPE :       ${sysData.portType}
      TIMER_DELAY :             ${sysData.timerDelay}

      ================================================================
    """.trimIndent())
    val repo = AdvertisementRepository(vertx.sharedData())
    this.vertx.deployVerticle(AdvertisementRequestProducer(sysData, repo))
    // Register koltin module for jackson
    DatabindCodec.mapper().registerModule(KotlinModule())
    if("cloudevents".equals(sysData.portType)){
      this.vertx.deployVerticle(AdvertisementCloudEventsRequester(sysData, repo))
    }else if("http".equals(sysData.portType)){
      this.vertx.deployVerticle(AdvertisementHTTPRequester(sysData, repo))
    }else{
      throw IllegalArgumentException("Port not supported")
      System.exit(1)
    }

  }
}
