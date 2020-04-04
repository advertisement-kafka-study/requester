package kafka.study.requester

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import kafka.study.requester.channel.cloudevents.AdvertisementCloudEventsRequester
import kafka.study.requester.channel.http.AdvertisementHTTPRequester
import kafka.study.requester.infra.SystemData
import kafka.study.requester.producer.AdvertisementRequestProducer
import java.lang.IllegalArgumentException
import java.util.*

class SiteRequester : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val sysData = SystemData(gatewayHost = System.getenv("GATEWAY_HOST"), customerKey = UUID.randomUUID().toString(), callback = System.getenv("MY_HOST"),portType =  System.getenv("PORT_TYPE"))
    val repo = AdvertisementRepository(vertx.sharedData())
    this.vertx.deployVerticle(AdvertisementRequestProducer(sysData, repo))

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
