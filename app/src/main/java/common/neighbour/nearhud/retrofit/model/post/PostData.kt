package common.neighbour.nearhud.retrofit.model.post

data class PostData(
    val createdOn: String,
    val updatedOn: String,
    val postId: Int,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val location: String,
    val bodyText: String,
    val image: String,
    val video: String,
    val userId: String,
    val groupId: String,
    val city: String,
    val state: String,
    val upvoteId: Boolean,
    val downvoteId: Boolean,
    val comment: ArrayList<Comments>,
    val userdetail: ArrayList<UserDetails>
)
