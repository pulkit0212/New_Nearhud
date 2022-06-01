package common.neighbour.nearhud.retrofit.model.comment

data class PostCommentData(
    val commentId: Int,
    val userId: String,
    val postId: Int,
    val createdOn: String,
    val updatedOn: String,
    val text: String,
    val firstName: String,
    val lastName: String,
    val profilePicture: String,
    val reply : ArrayList<PostCommentReplyData>,
    val replyCount:Int
)
