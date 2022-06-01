package common.neighbour.nearhud.retrofit.model.userprofile

data class UserProfileResponse(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Boolean,
    val code: Int
)