package common.neighbour.nearhud.retrofit.model.post

data class Comments(
    var commentId: Int,
    var userId: String,
    var postId: Int,
    var createdOn: String,
    var updatedOn: String,
    var text: String,
    var firstName: String,
    var lastName: String,
    var profilePicture: String
)
