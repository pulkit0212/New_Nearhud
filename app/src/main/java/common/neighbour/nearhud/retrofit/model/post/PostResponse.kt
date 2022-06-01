package common.neighbour.nearhud.retrofit.model.post

data class PostResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<Data>
//    val lastPostId:Int,
//    val totalPage:Int
)