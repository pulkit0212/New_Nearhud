package common.neighbour.nearhud.retrofit.model.group

data class LatLngResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: LatLngData
)
