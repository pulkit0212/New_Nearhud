package common.neighbour.nearhud.ui.map

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityMapsBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.RC_PLACE
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.SELECTED_PLACE_ID
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.SELECTED_PLACE_NAME
import common.neighbour.nearhud.retrofit.model.group.GroupInfoResponse
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.registration.RegistrationActivity2
import java.io.IOException
import java.util.*


class MapsActivity : BaseActivity(), OnMapReadyCallback,HomeNavigator {
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: HomeViewModel by viewModels()
    private var mMap: GoogleMap? = null

    private val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG)
    private lateinit var placesClient: PlacesClient

    var lat: ArrayList<String>? = null
    var lng: ArrayList<String>? = null
    var searchAaddress: String? = null

    var grpLatLng: ArrayList<String>? = null

    var searchLocation: LatLng? = null
    var zoomLevel = 14.0.toFloat()

    var groupInfoResponseModel: GroupInfoResponse? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    // [START maps_poly_activity_style_polygon]
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_GREEN_ARGB = -0x1000000
    private val COLOR_PURPLE_ARGB = -0xc7598f
    private val COLOR_ORANGE_ARGB = -0x1000000
    private val COLOR_BLUE_ARGB = -0xd33d1d
    private val COLOR_BLACK_ARGB = -0x1000000
//    private val COLOR_WHITE_ARGB = -0x1
//    private val COLOR_GREEN_ARGB = -0xc771c4
//    private val COLOR_PURPLE_ARGB = -0xc7598f
//    private val COLOR_ORANGE_ARGB = -0x1000000
//    private val COLOR_BLUE_ARGB = -0xd33d1d
//    private val COLOR_BLACK_ARGB = -0x1000000

    private val POLYGON_STROKE_WIDTH_PX = 6
    private val PATTERN_DASH_LENGTH_PX = 10
    private val PATTERN_GAP_LENGTH_PX = 0
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_maps)

        viewModel.navigator = this

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            if (binding.tvGrpName.text == "Not Found") {
                showCustomAlert("Please confirm your group",binding.root)
            }
            else {
                startActivityWithAnimation(
                    Intent(
                        this,
                        RegistrationActivity2::class.java
                    ), Appconstants.SLIDE_IN_LEFT
                )
            }
        })

        binding.btnConfirmAddress.setOnClickListener {
            callGetGroupAPI(Common.sCLat, Common.sCLng)
        }

        binding.llSearchLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, RC_PLACE)
        }
        binding.etSearchLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, RC_PLACE)
        }
    }

    private fun callNearbyGroup(grpID: String) {
        viewModel.stateAPI(grpID).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    if(it.data!!.data.isEmpty()){
                        Common.listState.clear()
                        //showCustomAlert(it?.message,binding.root)
                    }
                    else{
                        for (stateModel in it.data.data) {
                            Common.listState.clear()
                            Common.listState.add(stateModel.latLongAddress)
                            highLightNearbyGroups()
                        }

                    }
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun highLightNearbyGroups() {
        // highlight nearbyGroups
        binding.cvAddress.visibility = View.GONE
        binding.cvGroup.visibility = View.VISIBLE
        var index = 0
        while (index < Common.listState.size){
            //for (i in Common.listState) {
                lat = ArrayList(Arrays.asList(*Common.listState[index].split(";").toTypedArray()))
                // lng = ArrayList(Arrays.asList(*Common.listState[1].split(";").toTypedArray()))
                val polygonOptions = PolygonOptions()
                polygonOptions.clickable(true)
                for (i in lat!!.indices) {
                    val l = lat!![i].split(",").toTypedArray()[0]
                    val li = lat!![i].split(",").toTypedArray()[1]
                    val temp = LatLng(l.toDouble(), li.toDouble())
                    polygonOptions.add(temp)
                }
                val polygon1 = mMap!!.addPolygon(polygonOptions)
                // Store a data object with the polygon, used here to indicate an arbitrary type.

                polygon1.tag = "beta"
                stylePolygon(polygon1)
                index++
           // }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.clear()
        // Add a marker to current location and move the camera
        searchLocation = LatLng(Common.sLat, Common.sLng)
        //mMap.addMarker(new MarkerOptions().position(searchLocation).title("Current address"));
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation!!, zoomLevel))
        mMap!!.setOnCameraIdleListener(OnCameraIdleListener {
            val center = mMap!!.cameraPosition.target
            Common.sCLat = center.latitude
            Common.sCLng = center.longitude

            if (!Common.sCLat.equals(Common.sLat)){
                Common.sLat = center.latitude
                Common.sLng = center.longitude
                searchAaddress = completeAddress(center.latitude, center.longitude)
                binding.tvAddress.text = searchAaddress
                Common.address = searchAaddress
                binding.cvAddress.visibility = View.VISIBLE
                binding.cvGroup.visibility = View.GONE
                //callGetGroupAPI(center.latitude, center.longitude)
            }

        })
        if(Common.mapFlag == 0){
            callNearbyGroup(Common.grpID)
            highLightMyGroup(googleMap)
        }
    }

    private fun callGetGroupAPI(latitude: Double, longitude: Double) {
        viewModel.getGroupAPI(latitude, longitude).observe(this, {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    if (!it.data!!.data.exist) {
                        mMap!!.clear()
                        binding.cvAddress.visibility = View.GONE
                        binding.cvGroup.visibility = View.VISIBLE
                        Common.mapFlag = 1
                        binding.tvGrpName.text = "Not Found"
                        binding.tvWardId.text = ""
                        binding.tvCity.text = ""
                        binding.tvState.text = ""
                        showCustomAlert("Group not found", binding.root)
                    }
                    else{
                        binding.cvAddress.visibility = View.GONE
                        binding.cvGroup.visibility = View.VISIBLE
                        Common.savedGrpInfo = it.data.data
                        Common.grpID = it.data.data.groupId
                        Common.listGroupLatLng.clear()
                        Common.listGroupLatLng.add(it.data.data.latLongAddress)
                        Common.mapFlag = 0
                        onMapReady(mMap!!)
                    }
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
                }
            }
            })
    }

    private fun highLightMyGroup(googleMap: GoogleMap) {
        binding.cvAddress.visibility = View.GONE
        binding.cvGroup.visibility = View.VISIBLE
            for (j in Common.listGroupLatLng.indices) {
                grpLatLng = ArrayList(Arrays.asList(*Common.listGroupLatLng[0].split(";").toTypedArray()))
            }
            binding.tvGrpName.text = Common.savedGrpInfo.name
            binding.tvWardId.text = Common.savedGrpInfo.wardId
            binding.tvCity.text = Common.savedGrpInfo.city
            binding.tvState.text = Common.savedGrpInfo.state
            val polygonOptionsGrp = PolygonOptions()
            polygonOptionsGrp.clickable(true)
            for (i in grpLatLng!!.indices) {
                val l = grpLatLng!![i].split(",").toTypedArray()[0]
                val li = grpLatLng!![i].split(",").toTypedArray()[1]
                val temp = LatLng(l.toDouble(), li.toDouble())
                polygonOptionsGrp.add(temp)
            }
            val polygonGrp = googleMap.addPolygon(polygonOptionsGrp)
            // Store a data object with the polygon, used here to indicate an arbitrary type.

            polygonGrp.tag = "alpha"
            stylePolygon(polygonGrp)
    }

    private fun stylePolygon(polygon: Polygon) {
        var type = ""
        // Get the data object stored with the polygon.
        if (polygon.tag != null) {
            type = polygon.tag.toString()
        }
        var pattern: List<PatternItem?>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }
        polygon.setStrokePattern(pattern)
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    private fun completeAddress(latitude: Double, longitude: Double): String {
        var currentAddress = ""
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (currentAddress != null) {
                val returnAddress = addresses[0]
                val stringBuilderReturnAddress = StringBuilder()
                for (i in 0..returnAddress.maxAddressLineIndex) {
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n")
                }
                currentAddress = stringBuilderReturnAddress.toString()
            } else {
                Toast.makeText(this@MapsActivity, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@MapsActivity, e.message, Toast.LENGTH_SHORT).show()
        }
        return currentAddress
    }

    override fun onLoading(isLoading: Boolean) {
        loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,binding.root)
    }

    override fun Logout() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RC_PLACE) {
            if (data != null) {
                val tempId = data.getStringExtra(SELECTED_PLACE_ID)
                val tempName = data.getStringExtra(SELECTED_PLACE_NAME)
                binding.tvAddress.text = tempName
                Common.address = tempName
              //  binding.cvAddress.visibility = View.VISIBLE
              //  binding.cvGroup.visibility = View.GONE

                //lat lng by address
                var addressList: List<Address>? = null
                if (tempName != null || tempName != "") {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName(tempName, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (addressList!!.isEmpty()) {
                        Toast.makeText(
                            this@MapsActivity,
                            "Location not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val address = addressList[0]
                        searchLocation = LatLng(address.latitude, address.longitude)
                        //mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap!!.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                searchLocation,
                                zoomLevel
                            )
                        )
                        searchAaddress = completeAddress(address.latitude, address.longitude)
                        Common.sLat = address.latitude
                        Common.sLng = address.longitude
                    }
                } else {
                    Toast.makeText(this@MapsActivity, "Please Enter Location.", Toast.LENGTH_SHORT)
                        .show()
                }

//                val request = FetchPlaceRequest.newInstance(tempId!!, placeFields)
//                placesClient = Places.createClient(this@MapsActivity)
//                placesClient.fetchPlace(request)
//                    .addOnSuccessListener { response: FetchPlaceResponse ->
//                        val place = response.place
//                        llat = place.latLng!!.latitude.toString()
//                        llong = place.latLng!!.longitude.toString()
//                        mMap!!.animateCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                place.latLng!!,
//                                zoomLevel
//                            )
//                        )
//                        Log.i("LOCATION_UPDATE", "Place found: ${place.name}")
//                    }.addOnFailureListener { exception: Exception ->
//                        if (exception is ApiException) {
//                            Log.e("LOCATION_UPDATE", "Place not found: ${exception.message}")
//                            // val statusCode = exception.statusCode
//                        }
//                    }

//                searchLocation = LatLng(point.latitude,point.latitude)
//                //mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                mMap!!.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        searchLocation,
//                        zoomLevel
//                    )
//                )
                // searchAaddress = completeAddress(llat, llong)


//                Common.sCLat = point.latitude
//                Common.sCLng = point.latitude

            }
        }
    }

//    private fun getLocationFromAddress(tempName: String?): GeoPoint {
//
//        val coder = Geocoder(this)
//        val address: List<Address>?
//        var p1: GeoPoint? = null
//
//        try {
//            address = coder.getFromLocationName(tempName, 5)
//            val location = address[0]
//            location.latitude
//            location.longitude
//            p1 = GeoPoint(location.latitude, location.longitude)
//        }
//        catch (ex: IOException) {
//            ex.printStackTrace();
//        }
//
//        return p1!!
//    }
}