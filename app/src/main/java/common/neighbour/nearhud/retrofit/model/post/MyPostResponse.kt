package common.neighbour.nearhud.retrofit.model.post

data class MyPostResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<PostData>
)
