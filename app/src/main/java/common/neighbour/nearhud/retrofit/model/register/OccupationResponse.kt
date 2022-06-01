package common.neighbour.nearhud.retrofit.model.register


data class OccupationResponse (
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<String>
        )