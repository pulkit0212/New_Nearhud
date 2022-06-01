package common.neighbour.nearhud.ui.home.fragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.extensions.TimeConverter
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ItemHomeCommentBinding
import common.neighbour.nearhud.retrofit.model.post.CommentData
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeCommentAdapter (var context: Context) : RecyclerView.Adapter<HomeCommentAdapter.CommentViewHolder>() {

    private var commentData = ArrayList<CommentData>()
    private var viewModel: MainViewModel? = null
    private val inputdateformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val DATE_FORMAT = "yyyy-MM-dd HH:mm"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        var binding: ItemHomeCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_comment, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        var converteddate: Long? = 0L
        //commentData.reverse()
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
       // holder.binding.tvTime.text = Utility.getTimeGap(converteddate!!)
        holder.binding.tvTime.text = TimeConverter.timeDifference(commentData[position].createdOn)
        holder.binding.tvMsg.text = commentData[position].text
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

    fun setData(data: ArrayList<CommentData>, viewModelm: MainViewModel) {
        this.commentData = data
        this.viewModel = viewModelm
        notifyDataSetChanged()
    }

    class CommentViewHolder(var binding: ItemHomeCommentBinding) : RecyclerView.ViewHolder(binding.root)

}