package common.neighbour.nearhud.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.databinding.ItemTextPostBinding
import common.neighbour.nearhud.retrofit.model.post.Data

class PostTextAdapter(var context: Context, val onClick: OnClickInterface)
    : RecyclerView.Adapter<PostTextAdapter.PostViewHolder>() {

    private var postData = mutableListOf<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding: ItemTextPostBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_text_post, parent, false)
        return PostViewHolder(binding)
    }



    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.binding.onlyText.text = postData[position].bodyText
        holder.binding.etComment.setOnClickListener {
            onClick.onCommentClick(postData,position)
        }
    }


    override fun getItemCount(): Int {
        return postData.size
    }

    fun setData(data: MutableList<Data>) {
        this.postData = data
        notifyDataSetChanged()
    }



    class PostViewHolder(var binding: ItemTextPostBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickInterface {
        fun onCommentClick(postData: MutableList<Data>, position: Int)
    }

}

