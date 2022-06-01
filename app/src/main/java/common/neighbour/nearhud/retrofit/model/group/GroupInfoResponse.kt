package common.neighbour.nearhud.retrofit.model.group

data class GroupInfoResponse(
    val `data`: GroupInfo,
    val message: String,
    val status: Boolean,
    val code: Int
)
