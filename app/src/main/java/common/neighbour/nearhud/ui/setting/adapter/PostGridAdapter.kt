package common.neighbour.nearhud.ui.setting.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ItemGridThreeImageBinding
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.setting.MyPostActivity

class PostGridAdapter(var userId: String, var context: Context)
    : RecyclerView.Adapter<PostGridAdapter.PostViewHolder>() {

    private var postData = mutableListOf<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding: ItemGridThreeImageBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_grid_three_image, parent, false)
        return PostViewHolder(binding)
    }



    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        if (postData[position].video == ""){
            holder.binding.ivVideo.visibility = View.GONE
            Glide.with(context)
                .load(postData[position].image).placeholder(R.drawable.placeholder_image)
                .into(holder.binding.ivStoryImage)
        }
        else if(postData[position].image == ""){
            holder.binding.ivVideo.visibility = View.VISIBLE
            Glide.with(context)
                .load(postData[position].video).placeholder(R.drawable.placeholder_image)
                .into(holder.binding.ivStoryImage)
        }

        holder.itemView.setOnClickListener {
            Common.MY_POST_POSITION = holder.layoutPosition
            val intent = Intent(holder.itemView.context, MyPostActivity::class.java)
            intent.putExtra("user_id",userId)
            holder.itemView.context.startActivity(intent)

        }
    }


    override fun getItemCount(): Int {
        return postData.size
    }

    fun setData(data: MutableList<Data>) {
        this.postData = data
        notifyDataSetChanged()
    }



    class PostViewHolder(var binding: ItemGridThreeImageBinding) : RecyclerView.ViewHolder(binding.root)


}