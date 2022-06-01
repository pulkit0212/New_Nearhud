package common.neighbour.nearhud.retrofit.model.comment

import common.neighbour.nearhud.retrofit.model.post.CommentData

data class PostCommentResponse (
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<PostCommentData>
        )