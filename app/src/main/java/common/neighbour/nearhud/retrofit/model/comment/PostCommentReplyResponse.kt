package common.neighbour.nearhud.retrofit.model.comment

data class PostCommentReplyResponse (
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<PostCommentReplyData>
        )