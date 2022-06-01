package common.neighbour.nearhud.retrofit.model.indiacity

data class IndianCityResponseItem(
    val country_code: String,
    val country_id: Int,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val state_code: String,
    val state_id: Int
)