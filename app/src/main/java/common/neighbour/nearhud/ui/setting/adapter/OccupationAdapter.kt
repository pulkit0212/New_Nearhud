package common.neighbour.nearhud.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.databinding.ItemNeighbourBinding
import common.neighbour.nearhud.databinding.ItemOccupationBinding
import common.neighbour.nearhud.retrofit.model.neighbour.NeighbourData
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.home.fragment.adapter.PostAdapter
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel

class OccupationAdapter (var context: Context,private val onOccupationClick: OnOccupationClick) : RecyclerView.Adapter<OccupationAdapter.OccupationViewHolder>() {

    private var occupationData = ArrayList<String>()
    private var viewModel: MainViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OccupationViewHolder {
        var binding: ItemOccupationBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_occupation, parent, false)
        return OccupationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OccupationViewHolder, position: Int) {

        holder.binding.tvOccupation.text = occupationData[position]

        holder.binding.tvOccupation.setOnClickListener {
            onOccupationClick.onOccupationClick(occupationData[position])
        }

    }

    override fun getItemCount(): Int {
        return occupationData.size
    }

    fun setData(data: ArrayList<String>, viewModelm: MainViewModel) {
        this.occupationData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class OccupationViewHolder(var binding: ItemOccupationBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnOccupationClick{
        fun onOccupationClick(occupation:String)
    }
}