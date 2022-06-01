package common.neighbour.nearhud.newUi

import com.google.gson.annotations.SerializedName


data class PlacesResult(
    @SerializedName("formatted_address")
    var formatted_address: String?,
    @SerializedName("geometry")
    var geometry: Geometry,
    @SerializedName("icon")
    var icon: String?,
    @SerializedName("icon_background_color")
    var icon_background_color: String?,
    @SerializedName("icon_mask_base_uri")
    var icon_mask_base_uri: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("photos")
    var photos: List<Photo>?,
    @SerializedName("place_id")
    var place_id: String,
    @SerializedName("reference")
    var reference: String?,
    @SerializedName("types")
    var types: List<String>?
)