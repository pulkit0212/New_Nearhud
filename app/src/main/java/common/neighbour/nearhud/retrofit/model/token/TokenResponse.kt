package common.neighbour.nearhud.retrofit.model.token


data class TokenResponse (
    val message: String,
    val status: Boolean,
    val code: Int,
    val data: TokenData
        )

