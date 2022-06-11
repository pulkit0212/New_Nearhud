package common.neighbour.nearhud.retrofit.model.group

data class GroupInfo (
    var groupId: String,
    var wardId: String,
    var name: String,
    var city: String,
    var state: String,
    var nearbyWard: String,
    var latLongAddress: String,
    var exist: Boolean
    )