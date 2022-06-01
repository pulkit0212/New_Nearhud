package common.neighbour.nearhud.retrofit.model.post

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("postId") var postId: Int = 0,
    @SerializedName("createdOn") var createdOn: String = "",
    @SerializedName("updatedOn") var updatedOn: String = "",
    @SerializedName("upvoteCount") var upvoteCount: Int = 0,
    @SerializedName("downvoteCount") var downvoteCount: Int = 0,
    @SerializedName("location") var location: String = "",
    @SerializedName("bodyText") var bodyText: String = "",
    @SerializedName("image") var image: String = "",
    @SerializedName("video") var video: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("groupId") var groupId: String = "",
    @SerializedName("city") var city: String = "",
    @SerializedName("state") var state: String = "",
    @SerializedName("comment") var comment: ArrayList<CommentData>? = null,
    @SerializedName("commentCount") var commentCount: Int = 0,
    @SerializedName("upvoteId") var upvoteId: Boolean = false,
    @SerializedName("downvoteId") var downvoteId: Boolean = false,
    @SerializedName("userFullName") var fullName: String = "",
    @SerializedName("userProfilePic") var profile: String = ""
)
{
    constructor() : this (
        0,
        "",
        "",
        0,
        0,
        "",
        "",
        "",
        "",
        "", "", "", "", null,
        0,
        false,
        false,
        "",
    "")
}