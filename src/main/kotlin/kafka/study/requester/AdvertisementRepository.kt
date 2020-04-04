package kafka.study.requester

import com.google.common.hash.Hashing
import io.vertx.core.json.Json
import io.vertx.core.shareddata.SharedData
import kafka.study.requester.domain.AdvertisementRequest
import java.nio.charset.StandardCharsets

class AdvertisementRepository(val data:SharedData){

  fun add(opportunityId:String,adv: AdvertisementRequest){
    val advId = Hashing.sha256().hashString(Json.encode(adv), StandardCharsets.UTF_8).toString();
    this.data.getLocalMap<String,String>("advs").put(opportunityId,Json.encode(adv))
  }

  fun get(opportunityId:String): AdvertisementRequest {
    val data = this.data.getLocalMap<String, String>("advs").get(opportunityId)
    return Json.decodeValue(data, AdvertisementRequest::class.java)
  }

}
