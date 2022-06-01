package common.neighbour.nearhud.retrofit.model.post.vote

import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.retrofit.model.post.PostData


data class VoteResponse(
    val message: String,
    val status: Boolean,
    val code: Int,
    val `data`: ArrayList<Data>
)
