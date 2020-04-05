package kafka.study.requester.producer

import com.github.javafaker.Faker
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import kafka.study.requester.AdvertisementRepository
import kafka.study.requester.domain.AdvRequirements
import kafka.study.requester.domain.AdvertisementRequest
import kafka.study.requester.domain.CallbackData
import kafka.study.requester.infra.SystemData
import java.util.logging.Logger

class AdvertisementRequestProducer(private val config: SystemData, private val repo: AdvertisementRepository) : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementRequestProducer")

  override fun start() {
    val req = AdvRequirements(timeout = 1)
    val callbackData = CallbackData(config.callback)
    this.vertx.setPeriodic(config.timerDelay) {
      val sport = Faker().team().sport()
      LOGGER.info("Generating advertisement request for $sport")
      val adv = AdvertisementRequest(requirements = req, category = sport, callbackData = callbackData)
      this.vertx.eventBus().publish("new-adv-request",Json.encode(adv))
    }
  }

}
