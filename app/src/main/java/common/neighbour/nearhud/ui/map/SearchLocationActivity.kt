package common.neighbour.nearhud.ui.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import common.neighbour.nearhud.R
import common.neighbour.nearhud.databinding.ActivitySearchLocationBinding
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.RC_PLACE
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.SELECTED_PLACE_ID
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.SELECTED_PLACE_NAME

class SearchLocationActivity : AppCompatActivity() , PlacesApiAdapter.PlaceAutoCompleteInterface{

    private lateinit var binding: ActivitySearchLocationBinding
    private lateinit var placesApiAdapter : PlacesApiAdapter
    private lateinit var placesClient: PlacesClient
    private var places : ArrayList<AutocompletePrediction> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_search_location)
        Places.initialize(applicationContext, getString(R.string.places_api_key))
        placesClient = Places.createClient(this)
        binding.rvPlaces.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        placesApiAdapter = PlacesApiAdapter(this, places)
        binding.rvPlaces.adapter = placesApiAdapter
        firstSearch("jaipur")
        binding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 2) {
                    val token = AutocompleteSessionToken.newInstance()
                    val request = FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                        //.setLocationBias(bounds) //.setLocationRestriction(bounds)
                        .setCountry("in")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()
                    callPlacesListener(request)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.llBack.setOnClickListener{
            finish()
        }


    }

    private fun firstSearch(s: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
            //.setLocationBias(bounds) //.setLocationRestriction(bounds)
            .setCountry("in")
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setSessionToken(token)
            .setQuery(s)
            .build()
        callPlacesListener(request)
    }

    private fun callPlacesListener(request: FindAutocompletePredictionsRequest) {
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            places.clear()
            for (prediction in response.autocompletePredictions) {
                places.add(prediction)
                placesApiAdapter.notifyDataSetChanged()
                Log.i("PLACES_LOG", prediction.placeId)
                Log.i("PLACES_LOG", prediction.getPrimaryText(null).toString())
            }
        }.addOnFailureListener { exception: Exception? ->
            if (exception is ApiException) {
                val apiException = exception as ApiException
                Log.e("PLACES_LOG", "Place not found: " + apiException.statusCode)
            }
        }
    }

    override fun onPlaceClick(mResult: AutocompletePrediction) {
        val i = Intent()
        i.putExtra(SELECTED_PLACE_ID,mResult.placeId)
        i.putExtra(SELECTED_PLACE_NAME,mResult.getPrimaryText(null).toString())
        setResult(RC_PLACE,i)
        finish()
    }
}