package common.neighbour.nearhud.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import common.neighbour.nearhud.R


class PlacesApiAdapter(private var placesAutoCompleteInterface: PlaceAutoCompleteInterface,private var places : ArrayList<AutocompletePrediction>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(common.neighbour.nearhud.R.layout.rv_item_location, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       (holder as ItemHolder).bind(places[position])
        //(holder as ItemHolder).bind()
    }

    inner class ItemHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(autocompletePrediction: AutocompletePrediction) {
            view.apply {
                val tvLocation = findViewById<TextView>(R.id.tvLocation)
                tvLocation.text = autocompletePrediction.getPrimaryText(null).toString()
                setOnClickListener {
                    placesAutoCompleteInterface.onPlaceClick(autocompletePrediction)
                }
            }
        }
    }

    override fun getItemCount(): Int {
    return places.size
    }
    interface PlaceAutoCompleteInterface {
        fun onPlaceClick(mResult: AutocompletePrediction)
    }


}