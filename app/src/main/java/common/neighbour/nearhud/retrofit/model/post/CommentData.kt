package common.neighbour.nearhud.retrofit.model.post

import com.google.gson.annotations.SerializedName

data class CommentData(
    @SerializedName("commentId") var commentId: Int = 0,
    @SerializedName("userId") var userId: String = "",
    @SerializedName("postId") var postId: Int = 0,
    @SerializedName("createdOn") var createdOn: String = "",
    @SerializedName("updatedOn") var updatedOn: String = "",
    @SerializedName("text") var text: String = "",
    @SerializedName("firstName") var firstName: String = "",
    @SerializedName("lastName") var lastName: String = "",
    @SerializedName("profilePicture") var profilePicture: String = ""
)
{
    constructor():this(
        0,
        "",
        0,
        "",
        "",
        "",
        "",
        "",
        ""
    )
}
