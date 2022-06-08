package common.neighbour.nearhud.retrofit.model.contact_share

data class ReferNumberListResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<SortNumber>
)