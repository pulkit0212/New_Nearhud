package common.neighbour.nearhud.ui.setting.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ItemHelpBinding
import common.neighbour.nearhud.retrofit.model.userprofile.HelpData
import java.util.*
import android.view.View
import kotlin.collections.ArrayList

class HelpAdapter (var context: Context) : RecyclerView.Adapter<HelpAdapter.MyNeighbourViewHolder>() {

    private var helpData = ArrayList<HelpData>()
    private var viewModel: MainViewModel? = null

    var isView = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNeighbourViewHolder {
        var binding: ItemHelpBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_help, parent, false)
        return MyNeighbourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyNeighbourViewHolder, position: Int) {
        isView = false
        // Glide.with(context).load(commentData.get(position).profilePicture).into(holder.binding.ivProfileImage)
        holder.binding.tvReport.text = helpData[position].question
        holder.binding.tvHelpMaterial.text = helpData[position].answer

        holder.binding.clTitle.setOnClickListener{
            if(!isView){
                isView = true
                holder.binding.tvHelpMaterial.visibility = View.VISIBLE
                //imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.your_image))
            }
            else{
                isView = false
                holder.binding.tvHelpMaterial.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return helpData.size
    }

    fun setData(data: ArrayList<HelpData>, viewModelm: MainViewModel) {
        this.helpData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class MyNeighbourViewHolder(var binding: ItemHelpBinding) : RecyclerView.ViewHolder(binding.root)

}