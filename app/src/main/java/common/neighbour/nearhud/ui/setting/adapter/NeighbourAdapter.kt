package common.neighbour.nearhud.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ItemNeighbourBinding
import common.neighbour.nearhud.retrofit.model.neighbour.NeighbourData
import java.util.*
import kotlin.collections.ArrayList

class NeighbourAdapter (var context: Context) : RecyclerView.Adapter<NeighbourAdapter.MyNeighbourViewHolder>() {

    private var neighbourData = ArrayList<NeighbourData>()
    private var viewModel: MainViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNeighbourViewHolder {
        var binding: ItemNeighbourBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_neighbour, parent, false)
        return MyNeighbourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyNeighbourViewHolder, position: Int) {

        if(!neighbourData[position].profilePicture.isNullOrEmpty()){
            Glide.with(context).load(neighbourData[position].profilePicture).placeholder(R.drawable.user).into(holder.binding.ivProfile)
            // sharedPre.setEmailProfile(it.data[0].profilePicture)
        }else{
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivProfile)
        }

        // Glide.with(context).load(commentData.get(position).profilePicture).into(holder.binding.ivProfileImage)
        holder.binding.tvName.text = neighbourData[position].firstName+" "+neighbourData[position].lastName
        holder.binding.tvLocation.text = neighbourData[position].occupation

    }

    override fun getItemCount(): Int {
        return neighbourData.size
    }

    fun setData(data: ArrayList<NeighbourData>, viewModelm: MainViewModel) {
        this.neighbourData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class MyNeighbourViewHolder(var binding: ItemNeighbourBinding) : RecyclerView.ViewHolder(binding.root)

}