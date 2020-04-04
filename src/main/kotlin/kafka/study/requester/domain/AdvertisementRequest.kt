package kafka.study.requester.domain

data class AdvertisementRequest (val category:String, val requirements: AdvRequirements, val callbackData: CallbackData)

data class AdvRequirements(val timeout:Int)

data class CallbackData(val url:String)
