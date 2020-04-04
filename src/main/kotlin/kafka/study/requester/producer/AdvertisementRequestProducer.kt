package kafka.study.requester.producer

import com.github.javafaker.Faker
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import kafka.study.requester.AdvertisementRepository
import kafka.study.requester.domain.AdvRequirements
import kafka.study.requester.domain.AdvertisementRequest
import kafka.study.requester.domain.CallbackData
import kafka.study.requester.infra.SystemData

class AdvertisementRequestProducer(private val config: SystemData, private val repo: AdvertisementRepository) : AbstractVerticle() {

  override fun start() {
    val req = AdvRequirements(timeout = 1)
    val callbackData = CallbackData(config.callback)
    this.vertx.setPeriodic(100) {
      val sport = Faker().team().sport()
      val adv = AdvertisementRequest(requirements = req, category = sport, callbackData = callbackData)
      this.vertx.eventBus().publish("new-adv-request",Json.encode(adv))
    }
  }

}
