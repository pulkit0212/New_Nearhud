package common.neighbour.nearhud.ui.home.fragment.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import common.neighbour.nearhud.repositories.constance.AppConstance
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ItemPostBinding
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.home.fragment.create_post.CreatePostActivity
import java.lang.String
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import com.google.android.exoplayer2.SimpleExoPlayer
import common.neighbour.nearhud.core.extensions.TimeConverter
import common.neighbour.nearhud.database.prefrence.SharedPre


class PostAdapter(var context: Context,
                  private val onVoteClick: OnVoteClick,private val sharedPre: SharedPre)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postData = mutableListOf<Data>()
    private var viewModel: MainViewModel? = null
    private var userId: kotlin.String? = null
    var imageViewList: ArrayList<ImageView> = ArrayList()
    private var homeCommentAdapter: HomeCommentAdapter? = null

    private val inputdateformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

   // private val comment : MutableLiveData<CommentData>()

    //exo player
    var exoPlayer: SimpleExoPlayer? = null
    var isPlay = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding: ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_post, parent, false)
        return PostViewHolder(binding)
    }



    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
      //  holder.binding.vvPost.useController = false
        var commentCount = postData[position].commentCount.toString()
        if(Appconstants.POST_TYPE == 1){
            holder.binding.homeTextPost.visibility = View.GONE
        }
        else
        {
            if (position == 0){
                holder.binding.homeTextPost.visibility = View.VISIBLE
            }
            else{
                holder.binding.homeTextPost.visibility = View.GONE
            }
        }

        holder.binding.homeTextPost.setOnClickListener {
            Common.isPostUpdate = false
            val intent = Intent(context, CreatePostActivity::class.java)
            context.startActivity(intent)
        }
        //initializeCommentRecycler
        homeCommentAdapter = HomeCommentAdapter(context)
        holder.binding.rvHomeComment.adapter = homeCommentAdapter
        holder.binding.rvHomeComment.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        holder.binding.rvHomeComment.layoutManager = layoutManager


        holder.binding.tvName.text = postData[position].fullName

        if(postData[position].profile != ""){
            Glide.with(context).load(postData[position].profile).placeholder(R.drawable.user).into(holder.binding.ivProfile)
        }else{
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivProfile)
        }
        if(sharedPre.emailProfile.isNullOrEmpty()){
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivProfileComment)

        }else{
            Glide.with(context).load(sharedPre.emailProfile).placeholder(R.drawable.user).into(holder.binding.ivProfileComment)
        }
        if(!sharedPre.emailProfile.isNullOrEmpty()){
            Glide.with(context).load(sharedPre.emailProfile).placeholder(R.drawable.user).into(holder.binding.ivProfileImage)
        }else{
            Glide.with(context).load(R.drawable.user).placeholder(R.drawable.user).into(holder.binding.ivProfileImage)
        }

        holder.binding.tvUpvoteCount.text = postData[position].upvoteCount.toString()
        holder.binding.tvDownvoteCount.text = postData[position].downvoteCount.toString()
        if (postData[position].bodyText == ""){
            holder.binding.tvText.visibility = View.GONE
        }else{
            holder.binding.tvText.visibility = View.VISIBLE
            holder.binding.tvText.text = postData[position].bodyText.linkify {
                onVoteClick.onTagClick(postData[position].userId,it)
            }
            holder.binding.tvText.movementMethod = LinkMovementMethod.getInstance()
        }
        if(postData[position].location == ""){
            holder.binding.tvLocation.visibility = View.GONE
        }
        else{
            holder.binding.tvLocation.visibility = View.VISIBLE
            holder.binding.tvLocation.text = postData[position].location
        }

        //
        if(commentCount == "0"){
            holder.binding.viewAllComment.visibility = View.GONE
        }
        else if(commentCount == "1" || commentCount == "2"){
            //holder.binding.viewAllComment.visibility = View.GONE
            holder.binding.viewAllComment.text = "View $commentCount comment"
        }
        else{
            holder.binding.viewAllComment.visibility = View.VISIBLE
            holder.binding.viewAllComment.text = "View all $commentCount comments"
        }

        //set time
        var converteddate: Long? = 0L
        try {
            converteddate = formatDateFromDateString(
                inputdateformat,
                DATE_FORMAT,
                postData[position].createdOn
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
       // holder.binding.tvTime.text = Utility.getTimeGap(converteddate!!)
        holder.binding.tvTime.text = TimeConverter.timeDifference(postData[position].createdOn)

        imageViewList.add(position,holder.binding.ivUpvoteImg)
        imageViewList.add(position,holder.binding.ivDownvoteImg)

        //action.onClickListener(imageViewList,position)

        if (postData[position].image == "" && postData[position].video == ""){
            holder.binding.view6.visibility = View.GONE
            holder.binding.counter.visibility = View.GONE
            holder.binding.ivPost.visibility = View.GONE
            holder.binding.vvPost.visibility = View.GONE
            holder.binding.onlyText.visibility = View.VISIBLE
            holder.binding.tvText.visibility=View.GONE
            holder.binding.onlyText.text = postData[position].bodyText.linkify {
                onVoteClick.onTagClick(postData[position].userId,it)
            }
            holder.binding.onlyText.movementMethod = LinkMovementMethod.getInstance()
        }

        else{
            holder.binding.view6.visibility = View.VISIBLE
            holder.binding.counter.visibility = View.VISIBLE
            if (postData[position].image == "") {
                holder.binding.ivPost.visibility = View.GONE
                holder.binding.vvPost.visibility = View.VISIBLE
                holder.binding.onlyText.visibility = View.GONE
                //holder.binding.tvText.visibility=View.VISIBLE
//                val proxy: HttpProxyCacheServer = getProxy(context)!!
//                holder.binding.vvPost.setUp(postData[position].video,"",
//                    Jzvd.SCREEN_NORMAL
//                )
//                 holder.binding.vvPost.thumbImageView.setImageURI(Uri.parse(postData[position].video))

                //exo player
                //
//                try {
//
//                    val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
//                    val trackSelector: TrackSelector =
//                        DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
//                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
//                    val videouri: Uri = Uri.parse(postData[position].video)
//                    val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")
//                    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
//                    val mediaSource: MediaSource =
//                        ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null)
//                    holder.binding.vvPost.player = exoPlayer
//                    exoPlayer!!.prepare(mediaSource)
//                    exoPlayer!!.playWhenReady = true
//                } catch (e: Exception) {
//
//                    //Log.e("TAG", "Error : $e")
//                }

                holder.binding.exoPlayer.imageView = holder.binding.ivThumbImage
                Glide.with(context).load(postData[position].video).into(holder.binding.ivThumbImage)
                holder.binding.exoPlayer.url = postData[position].video
                holder.binding.vvPost.setOnClickListener {
                    holder.binding.ivMute.visibility = View.VISIBLE
                    Thread(Runnable {
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            holder.binding.ivMute.visibility = View.GONE
                        }, 2000)
                    }).start()
                    if(holder.binding.exoPlayer.isMute){
                        holder.binding.exoPlayer.isMute =  false
                        AppConstance.IS_MUTE = false
                        holder.binding.ivMute.setImageDrawable(context.getDrawable(R.drawable.ic_speaker_on))
                    } else{
                        holder.binding.exoPlayer.isMute = true
                        AppConstance.IS_MUTE = true
                        holder.binding.ivMute.setImageDrawable(context.getDrawable(R.drawable.ic_speaker_off))
                    }
                }
            }
            else {
                holder.binding.ivPost.visibility = View.VISIBLE
                holder.binding.vvPost.visibility = View.GONE
                holder.binding.onlyText.visibility = View.GONE
                //holder.binding.tvText.visibility=View.VISIBLE
                Glide.with(context).load(postData[position].image).into(holder.binding.ivPost)

            }
        }

        if(postData[position].upvoteId){
            holder.binding.ivUpvoteImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.upvote))
        }
        else{
            holder.binding.ivUpvoteImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.up))
        }
        if(postData[position].downvoteId){
            holder.binding.ivDownvoteImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.downvote))
        }
        else{
            holder.binding.ivDownvoteImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.down))
        }

        holder.binding.ivUpvoteImg.setOnClickListener {
            onVoteClick.upVoteClick(position,postData[position].postId,
                postData[position].userId,
                holder.binding.tvUpvoteCount,holder.binding.tvDownvoteCount,
                holder.binding.ivUpvoteImg,holder.binding.ivDownvoteImg)
        }
        holder.binding.ivDownvoteImg.setOnClickListener {
            onVoteClick.downVoteClick(position,postData[position].postId,
                postData[position].userId!!,holder.binding.tvDownvoteCount,
                holder.binding.tvUpvoteCount,holder.binding.ivUpvoteImg,holder.binding.ivDownvoteImg)
        }

        holder.binding.ivComment.setOnClickListener {
           // Common.allComments.clear()
           // Common.allComments.addAll(postData[position].comment!!)
            Common.userIdForComment = userId!!
            Common.postIdForComment = String.valueOf(postData[position].postId)
            Common.post.clear()
            Common.post.add(postData[position])
            onVoteClick.onCommentClick(position)
        }

        holder.binding.etComment.setOnClickListener {
            //Common.allComments.clear()
            //Common.allComments.addAll(postData[position].comment!!)
            Common.userIdForComment = userId!!
            Common.postIdForComment = String.valueOf(postData[position].postId)
            Common.post.clear()
            Common.post.add(postData[position])
            onVoteClick.onCommentClick(position)
        }
        holder.binding.viewAllComment.setOnClickListener {
            //Common.allComments.clear()
            //Common.allComments.addAll(postData[position].comment!!)
            Common.userIdForComment = postData[position].userId!!
            Common.postIdForComment = String.valueOf(postData[position].postId)
            Common.post.clear()
            Common.post.add(postData[position])
            //exoPlayer!!.playWhenReady = false
            onVoteClick.onCommentClick(position)
        }
        // set data to comment
       // homeCommentAdapter!!.setData(postData[position].comment!!, viewModel!!)

        holder.binding.icDot.setOnClickListener {
            onVoteClick.onThreeDotClick(position,postData[position])
        }

    }


    fun getProxy(context: Context): HttpProxyCacheServer? {
        val app = context.applicationContext as common.neighbour.nearhud.application.MyApplication
        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(context)
    }

    @Throws(ParseException::class)
    fun formatDateFromDateString(
        inputDateFormat: kotlin.String?, outputDateFormat: kotlin.String?,
        inputDate: kotlin.String?
    ): Long? {
        val mParsedDate: Date
        val mInputDateFormat = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        return mParsedDate.time
    }

    override fun getItemCount(): Int {
        return postData.size
    }

    fun setData(data: MutableList<Data>, viewModelm: MainViewModel, userIdLm: kotlin.String?) {
        this.postData = data
        this.viewModel = viewModelm
        this.userId = userIdLm
        notifyDataSetChanged()
    }

    class PostViewHolder(var binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

interface OnVoteClick{
    fun upVoteClick(position:Int,postId: Int, userId: kotlin.String?, uptext: TextView, downtext: TextView, upImage: ImageView, downImage: ImageView)
    fun downVoteClick(position: Int,postId: Int, userId: kotlin.String, downtext: TextView, uptext: TextView, upImage: ImageView, downImage: ImageView)
    fun onCommentClick(position: Int)
    fun onTagClick(userId:kotlin.String,tag:kotlin.String)
    fun onThreeDotClick(position: Int, data: Data) {
    }
}

}

fun kotlin.String.linkify(linkColor:Int = Color.BLUE, linkClickAction:((kotlin.String) -> Unit)? = null): SpannableStringBuilder {
    val builder = SpannableStringBuilder(this)
    val matcher = Pattern.compile("(?:^|\\s+)(?:(?<mention>@)|(?<hash>#))(?<item>\\w+)(?=\\s+)").matcher(this)
    // val matcher = Patterns.WEB_URL.matcher(this)
    while(matcher.find()){
        val start = matcher.start()
        val end = matcher.end()
        builder.setSpan(ForegroundColorSpan(Color.BLUE),start,end,0)
        val onClick = object : ClickableSpan(){
            val group = matcher.group()
            override fun onClick(p0: View) {
                linkClickAction?.invoke(group)
            }
        }
        builder.setSpan(onClick,start,end,0)
        //builder.setSpan(onClick,start,end,0)
    }
    return builder
}
