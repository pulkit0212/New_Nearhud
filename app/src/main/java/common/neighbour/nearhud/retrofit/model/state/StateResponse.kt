package common.neighbour.nearhud.retrofit.model.state

data class StateResponse(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Boolean,
    val code:Int
)
