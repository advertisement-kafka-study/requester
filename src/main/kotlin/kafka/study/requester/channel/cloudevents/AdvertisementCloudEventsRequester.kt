package kafka.study.requester.channel.cloudevents

import io.cloudevents.CloudEvent
import io.cloudevents.extensions.DistributedTracingExtension
import io.cloudevents.extensions.ExtensionFormat
import io.cloudevents.http.vertx.VertxCloudEvents
import io.cloudevents.v02.AttributesImpl
import io.cloudevents.v02.CloudEventBuilder
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.json.Json
import io.vertx.ext.web.client.WebClient
import kafka.study.requester.AdvertisementRepository
import kafka.study.requester.domain.AdvertisementRequest
import kafka.study.requester.infra.SystemData
import java.net.URI
import java.util.*

class AdvertisementCloudEventsRequester(private val config: SystemData, private val repo: AdvertisementRepository) : AbstractVerticle() {

  override fun start() {
    vertx.eventBus().consumer<String>("new-adv-request") {
      val adv = Json.decodeValue(it.body(), AdvertisementRequest::class.java)

      val request: HttpClientRequest = vertx.createHttpClient().post(8080, config.gatewayHost, "/")

      val handler = request.handler({ resp -> })

      val dt = DistributedTracingExtension()
      dt.traceparent = UUID.randomUUID().toString()
      dt.tracestate = UUID.randomUUID().toString()

      val tracing: ExtensionFormat = DistributedTracingExtension.Format(dt)

      val cloudEvent: CloudEvent<AttributesImpl, String> = CloudEventBuilder.builder<String>()
        .withSource(URI.create("https://kafka.study/advertisement-gateway"))
        .withContenttype("application/json")
        .withId(adv.category)
        .withData(Json.encode(adv))
        .withExtension(tracing)
        .withType("kafka.study.adv.request").build()

      VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request)
      request.end()


    }
  }
}
