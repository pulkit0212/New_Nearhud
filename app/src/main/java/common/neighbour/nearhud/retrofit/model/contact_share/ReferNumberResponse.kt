package common.neighbour.nearhud.retrofit.model.contact_share

data class ReferNumberResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<String>
)
