package common.neighbour.nearhud.retrofit.model.login

data class OtpResponse(
    val `data`: OtpData,
    val message: String,
    val status: Boolean,
    val code:Int
)
