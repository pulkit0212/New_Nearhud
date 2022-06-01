package common.neighbour.nearhud.ui.registration

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityRegistration1Binding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.map.MapsActivity
import java.io.IOException

class RegistrationActivity1 : BaseActivity(){

    private lateinit var binding: ActivityRegistration1Binding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var  context: Context
    var curLatLng: LatLng? = null
    var curLatLng1: LatLng? = null
    var area: String? = null
    var district: String? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_registration1)
        context=this
        //viewModel.navigator=thisbtn_verify
        AccessClickLIstener()
    }

//    private fun Observers() {
//        callStateAPI()
//    }

    private fun AccessClickLIstener() {
        binding.cvNeighbour.setOnClickListener {
            if(binding.etEmail.text.toString().isNullOrEmpty()){
                showCustomAlert(getString(R.string.email_empty),binding.root)
                binding.etEmail.requestFocus()
            }else if(!methods.isValidEmail(binding.etEmail.text.toString())){
                showCustomAlert(getString(R.string.invalid_email),binding.root)
                binding.etEmail.requestFocus()
            }
            else if(binding.etAddress.text.toString().isNullOrEmpty()){
                showCustomAlert(getString(R.string.address_empty),binding.root)
                binding.etAddress.requestFocus()
            }
            else if(binding.etZip.text.toString().isNullOrEmpty()){
                showCustomAlert(getString(R.string.empty_zip),binding.root)
                binding.etZip.requestFocus()
            }
            else if(binding.etZip.text.toString().length<6){
                showCustomAlert(getString(R.string.empty_zip2),binding.root)
                binding.etZip.requestFocus()
            }
            else{
                // callPinCodeAPI()
                Common.email = binding.etEmail.text.toString()
                Common.address = binding.etAddress.text.toString()
                Common.zip = binding.etZip.text.toString()
                getLatLngFromAddress(binding.etAddress.text.toString(),binding.etZip.text.toString())
//                curLatLng = getLocationFromAddress(this, binding.etAddress.text.toString())
//                if (curLatLng == null) {
//                    showCustomAlert("No address found",binding.root)
//                    //callPinCodeAPI()
//                }
//                else {
//                    Common.userLatlng += curLatLng!!.latitude
//                    Common.userLatlng += ","
//                    Common.userLatlng += curLatLng!!.longitude
//
//                    Common.sLat = curLatLng!!.latitude
//                    Common.sLng = curLatLng!!.longitude
//                    Common.sCLat = curLatLng!!.latitude
//                    Common.sCLng = curLatLng!!.longitude
//                    //callPinCodeAPI()
//                    //callStateAPI()
//                    callGetGroupAPI(curLatLng!!.latitude, curLatLng!!.longitude)
//                }
            }

        }
    }


    private fun callPinCodeAPI() {
        viewModel.pincodeAPI(binding.etZip.text.toString()).observe(this, Observer {
            if(it.PostOffice!=null){
                area = it.PostOffice[0].Name
                district = it.PostOffice[0].District
                curLatLng = getLocationFromAddress(this, "$area $district")
                if (curLatLng == null) {
                    showCustomAlert("No address found",binding.root)
                    //callPinCodeAPI()
                }
                else {
                    Common.userLatlng += curLatLng!!.latitude
                    Common.userLatlng += ","
                    Common.userLatlng += curLatLng!!.longitude

                    Common.sLat = curLatLng!!.latitude
                    Common.sLng = curLatLng!!.longitude
                    Common.sCLat = curLatLng!!.latitude
                    Common.sCLng = curLatLng!!.longitude
                    //callPinCodeAPI()
                    //callStateAPI()
                    callGetGroupAPI(curLatLng!!.latitude, curLatLng!!.longitude)
                }
            }
            else{
                showCustomAlert("Zip code not found",binding.root)
            }
        })

    }

    private fun callStateAPI() {
//        viewModel.stateAPI(state!!, sharedPre.userId!!).observe(this, Observer {
//            if(it!=null){
//                for (stateModel in it.data) {
//                    Common.listState.add(stateModel.latLongAddress)
//                }
//                Common.listGroupLatLng.clear()
//                Common.mapFlag = 1
//                //showCustomAlert("No group found fogetGroupAPIr given address",binding.root)
//                startActivityWithAnimation(
//                    Intent(
//                        this,
//                        MapsActivity::class.java
//                    ), Appconstants.SLIDE_IN_LEFT
//                )
//            }
//            else{
//                showCustomAlert(it?.message,binding.root)
//            }
//        })

    }

    private fun callGetGroupAPI(latitude: Double, longitude: Double) {
        viewModel.getGroupAPI(latitude, longitude).observe(this, {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    if(!it.data!!.data.exist){
                        Common.listGroupLatLng.clear()
                        Common.mapFlag = 1
                        showCustomAlert("No group found for given address",binding.root)
                        startActivityWithAnimation(
                            Intent(
                                this,
                                MapsActivity::class.java
                            ),Appconstants.SLIDE_IN_LEFT
                        )
                        //startFragment(MapsFragment.newInstance(viewModel!!),true, MapsFragment.toString())
                    }
                    else{
                        Common.listGroupLatLng.clear()
                        Common.mapFlag = 0
                        Common.savedGrpInfo = it.data!!.data
                        Common.grpID = it.data!!.data.groupId
                        Common.listGroupLatLng.add(it.data.data.latLongAddress)
                        startActivityWithAnimation(
                            Intent(
                                this,
                                MapsActivity::class.java
                            ),Appconstants.SLIDE_IN_LEFT
                        )
                    }
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getLatLngFromAddress(address: String, zip: String) {
        viewModel.getLatLngFromAddress(address,zip).observe(this, Observer {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    if (it.data!!.data.dataFound) {
                        Common.userLatlng += it.data.data.geoCode.latitude
                        Common.userLatlng += ","
                        Common.userLatlng += it.data.data.geoCode.longitude

                        Common.sLat = it.data.data.geoCode.latitude
                        Common.sLng = it.data.data.geoCode.longitude
                        Common.sCLat = it.data.data.geoCode.latitude
                        Common.sCLng = it.data.data.geoCode.longitude
                        //callPinCodeAPI()
                        //callStateAPI()
                        callGetGroupAPI(it.data.data.geoCode.latitude, it.data.data.geoCode.longitude)
                    }
                    else {
                        showCustomAlert("No address found",binding.root)
                    }
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message,Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)

            if (address == null) {
                //return null;
                showCustomAlert("Given address not found",binding.root)
            }
            else
            {
                if (address.isEmpty()) {
                    showCustomAlert("Given address not found",binding.root)
                }
                else {
                    val location = address[0]
                    p1 = LatLng(location.latitude, location.longitude)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }


}