package common.neighbour.nearhud.ui.home.fragment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.core.extensions.TimeConverter
import common.neighbour.nearhud.databinding.ItemCommentBinding
import common.neighbour.nearhud.module.Utility
import common.neighbour.nearhud.retrofit.model.comment.PostCommentData
import common.neighbour.nearhud.ui.comment.ReplyActivity
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentAdapter (var context: Context) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentData = ArrayList<PostCommentData>()
    private var viewModel: HomeViewModel? = null
    private val inputdateformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private var replyCommentAdapter: ReplyCommentAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        var binding: ItemCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_comment, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        //initializeCommentRecycler
        replyCommentAdapter = ReplyCommentAdapter(context)
        holder.binding.rvCommentReply.adapter = replyCommentAdapter
        holder.binding.rvCommentReply.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        holder.binding.rvCommentReply.layoutManager = layoutManager

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
            Glide.with(context).load(commentData[position].profilePicture).placeholder(R.drawable.user).into(holder.binding.ivProfileImage)
            // sharedPre.setEmailProfile(it.data[0].profilePicture)
        }else{
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivProfileImage)
        }

        // Glide.with(context).load(commentData.get(position).profilePicture).into(holder.binding.ivProfileImage)
        holder.binding.tvName.text = commentData[position].firstName+" "+commentData[position].lastName

      //  holder.binding.tvTime.text = Utility.getTimeGap(converteddate!!)
        holder.binding.tvTime.text = TimeConverter.timeDifference(commentData[position].createdOn)
        holder.binding.tvMsg.text = commentData[position].text
        holder.binding.tvReply.visibility = View.VISIBLE
        if (commentData[position].replyCount > 1){
            holder.binding.tvReplyCount.visibility = View.VISIBLE
            holder.binding.tvReplyCount.text="View all ${commentData[position].replyCount.toString()} replies"
        }
        else{
            holder.binding.tvReplyCount.visibility = View.GONE
        }
        holder.binding.tvReplyCount.setOnClickListener {
            Common.commentIdForReply = commentData[position].commentId.toString()
            Common.commentText = commentData[position].text.toString()
            (context as BaseActivity).startActivityWithAnimation(
                Intent(
                    context,
                    ReplyActivity::class.java
                ), Appconstants.SLIDE_IN_TOP
            )
        }
        holder.binding.tvReply.setOnClickListener {
            Common.commentIdForReply = commentData[position].commentId.toString()
            Common.commentText = commentData[position].text.toString()
            (context as BaseActivity).startActivityWithAnimation(
                Intent(
                    context,
                    ReplyActivity::class.java
                ), Appconstants.SLIDE_IN_TOP
            )
        }

        // set data to comment
        replyCommentAdapter!!.setData(commentData[position].reply, viewModel!!)
        replyCommentAdapter!!.notifyDataSetChanged()

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

    fun setData(data: ArrayList<PostCommentData>, viewModelm: HomeViewModel) {
        this.commentData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class CommentViewHolder(var binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

}