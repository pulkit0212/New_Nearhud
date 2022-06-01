package common.neighbour.nearhud.retrofit.model.group

data class GroupInfo (
    val groupId: String,
    val wardId: String,
    val name: String,
    val city: String,
    val state: String,
    val nearbyWard: String,
    val latLongAddress: String,
    val exist: Boolean
    )