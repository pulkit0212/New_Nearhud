package common.neighbour.nearhud.retrofit.model.welcome


data class LaunchResponse (
    val message: String,
    val status: Boolean,
    val code: Int,
    val data: ArrayList<Launch>
        )