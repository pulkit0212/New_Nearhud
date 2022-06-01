package common.neighbour.nearhud.retrofit.model.neighbour


data class NeighbourResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<NeighbourData>
)
