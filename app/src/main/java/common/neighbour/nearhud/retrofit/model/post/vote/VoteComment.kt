package common.neighbour.nearhud.retrofit.model.post.vote

data class VoteComment (
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