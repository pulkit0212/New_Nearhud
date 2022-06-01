package common.neighbour.nearhud.retrofit.model.state

data class PincodeResponse (
    val PostOffice: ArrayList<PostOfficeData>,
    val Message: String,
    val Status: Boolean
        )