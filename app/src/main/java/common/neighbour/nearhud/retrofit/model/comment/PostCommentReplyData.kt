package common.neighbour.nearhud.retrofit.model.comment

data class PostCommentReplyData(
    val replyId: Int,
    val commentId: Int,
    val userId: String,
    val postId: Int,
    val createdOn: String,
    val updatedOn: String,
    val text: String,
    val firstName: String,
    val lastName: String,
    val profilePicture: String
)
