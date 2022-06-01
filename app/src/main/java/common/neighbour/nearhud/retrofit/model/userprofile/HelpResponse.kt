package common.neighbour.nearhud.retrofit.model.userprofile

data class HelpResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val data: ArrayList<HelpData>
)