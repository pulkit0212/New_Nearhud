package common.neighbour.nearhud.retrofit.model.token

data class CheckUserResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val data: Data
)