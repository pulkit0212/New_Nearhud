package common.neighbour.nearhud.ui.home.fragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.extensions.TimeConverter
import common.neighbour.nearhud.databinding.ItemCommentBinding
import common.neighbour.nearhud.databinding.ItemReplyCommentBinding
import common.neighbour.nearhud.module.Utility
import common.neighbour.nearhud.retrofit.model.comment.PostCommentData
import common.neighbour.nearhud.retrofit.model.comment.PostCommentReplyData
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReplyAdapter (var context: Context) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    private var commentData = ArrayList<PostCommentReplyData>()
    private var viewModel: HomeViewModel? = null
    private val inputdateformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        var binding: ItemReplyCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_reply_comment, parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {

        var converteddate: Long? = 0L
        try {
            converteddate = formatDateFromDateString(
                inputdateformat,
                DATE_FORMAT,
                commentData[position].createdOn
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if(!commentData[position].profilePicture.isNullOrEmpty()){
            Glide.with(context).load(commentData[position].profilePicture).placeholder(R.drawable.user).into(holder.binding.ivReplyProfileImage)
            // sharedPre.setEmailProfile(it.data[0].profilePicture)
        }else{
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivReplyProfileImage)
        }

        // Glide.with(context).load(commentData.get(position).profilePicture).into(holder.binding.ivProfileImage)
        holder.binding.tvReplyName.text = commentData[position].firstName+" "+commentData[position].lastName
       // holder.binding.tvReplyTime.text = Utility.getTimeGap(converteddate!!)
        holder.binding.tvReplyTime.text = TimeConverter.timeDifference(commentData[position].createdOn)
        holder.binding.tvReplyMsg.text = commentData[position].text

    }

    @Throws(ParseException::class)
    fun formatDateFromDateString(
        inputDateFormat: String?, outputDateFormat: String?,
        inputDate: String?
    ): Long? {
        val mParsedDate: Date
        val mOutputDateString: String
        val mInputDateFormat = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat(outputDateFormat, Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        //mOutputDateString = mOutputDateFormat.format(mParsedDate)
        return mParsedDate.time
    }

    override fun getItemCount(): Int {
        return commentData.size
    }

    fun setData(data: ArrayList<PostCommentReplyData>, viewModelm: HomeViewModel) {
        this.commentData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class ReplyViewHolder(var binding: ItemReplyCommentBinding) : RecyclerView.ViewHolder(binding.root)

}