package common.neighbour.nearhud.ui.comment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Handler
import android.os.Looper
import common.neighbour.nearhud.repositories.constance.AppConstance
//import cn.jzvd.JZVideoPlayerStandard
import cn.jzvd.Jzvd
import com.bumptech.glide.Glide
import android.text.method.LinkMovementMethod
import com.danikula.videocache.HttpProxyCacheServer
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityCommentBinding
import common.neighbour.nearhud.ui.home.fragment.adapter.CommentAdapter
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.widget.Toast.makeText
import com.google.android.material.snackbar.Snackbar
import common.neighbour.nearhud.retrofit.model.post.CommentData
import java.util.regex.Pattern
import android.widget.ScrollView




class CommentActivity : BaseActivity(), HomeNavigator {
    private lateinit var binding: ActivityCommentBinding
    private val viewModel: HomeViewModel by viewModels()
    private var commentAdapter: CommentAdapter? = null
    //exo player
    var exoPlayer: SimpleExoPlayer? = null
    var isPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_comment)

        viewModel.navigator=this
        WorkStation()

        binding.scrollView.postDelayed(Runnable { binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 1000)

        //startFragment(CommentFragment.newInstance(viewModel),true, CommentFragment.toString())
    }

    override fun onResume() {
        super.onResume()
        callPostCommentsAPI()
    }

    private fun WorkStation() {
        binding.vvPost.useController = false
       // binding.tvName.text = Common.post[0].userdetail[0].firstName+" "+Common.post[0].userdetail[0].lastName
        binding.tvName.text = Common.post[0].fullName

        if(Common.post[0].profile != ""){
            Glide.with(this).load(Common.post[0].profile).placeholder(R.drawable.user).into(binding.ivProfile)
        }else{
            Glide.with(this).load(R.drawable.user).placeholder(R.drawable.user).into(binding.ivProfile)
        }
        if(!sharedPre().emailProfile.isNullOrEmpty()){
            Glide.with(this).load(sharedPre().emailProfile).placeholder(R.drawable.user).into(binding.ivProfileComment)
        }else{
            Glide.with(this).load(R.drawable.user).placeholder(R.drawable.user).into(binding.ivProfileComment)
        }

        binding.tvLocation.text = Common.post[0].location
        binding.tvText.text =  Common.post[0].bodyText.linkify {
            //onVoteClick.onTagClick(item.userId,it)
        }
        binding.tvText.movementMethod = LinkMovementMethod.getInstance()

        binding.ivBack.setOnClickListener {
            finish()
            this.overridePendingTransition(  R.anim.slide_down,
                R.anim.slide_out_down)
        }

        if ( Common.post[0].image == "" && Common.post[0].video == ""){

            binding.ivPost.visibility = View.GONE
            binding.vvPost.visibility = View.GONE
            binding.onlyText.visibility = View.VISIBLE
            binding.tvText.visibility= View.GONE

            binding.onlyText.text =  Common.post[0].bodyText
        }

        else{
            if (Common.post[0].image == "") {
                binding.ivPost.visibility = View.GONE
                binding.vvPost.visibility = View.VISIBLE
                binding.onlyText.visibility = View.GONE
                binding.tvText.visibility= View.VISIBLE
//                val proxy: HttpProxyCacheServer = getProxy(this)!!
//                binding.vvPost.setUp(Common.post[0].video,"",
//                    //proxy.getProxyUrl( ),
//                    Jzvd.SCREEN_NORMAL
//                )
                //binding.vvPost.thumbImageView.setImageResource(R.drawable.thumb)
                try {
                    val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
                    val trackSelector: TrackSelector =
                        DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
                    val videouri: Uri = Uri.parse(Common.post[0].video)
                    val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")
                    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
                    val mediaSource: MediaSource =
                        ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null)
                    binding.vvPost.player = exoPlayer
                    exoPlayer!!.prepare(mediaSource)
                    exoPlayer!!.playWhenReady = true
                } catch (e: Exception) {
                    //Log.e("TAG", "Error : $e")
                }
//
//                binding.exoPlayer.imageView = binding.ivThumbImage
//                Glide.with(this).load(Common.post[0].video).into(binding.ivThumbImage)
//                binding.exoPlayer.url = Common.post[0].video
//                binding.vvPost.setOnClickListener {
//                    binding.ivMute.visibility = View.VISIBLE
//                    Thread(Runnable {
//                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//                            binding.ivMute.visibility = View.GONE
//                        }, 2000)
//                    }).start()
//                    if(binding.exoPlayer.isMute){
//                        binding.exoPlayer.isMute =  false
//                        AppConstance.IS_MUTE = false
//                        binding.ivMute.setImageDrawable(this.getDrawable(R.drawable.ic_speaker_on))
//                    } else{
//                        binding.exoPlayer.isMute = true
//                        AppConstance.IS_MUTE = true
//                        binding.ivMute.setImageDrawable(this.getDrawable(R.drawable.ic_speaker_off))
//                    }
//                }

            }
            else {
                binding.ivPost.visibility = View.VISIBLE
                binding.vvPost.visibility = View.GONE
                binding.onlyText.visibility = View.GONE
                binding.tvText.visibility= View.VISIBLE
                Glide.with(this).load( Common.post[0].image).into(binding.ivPost)

            }
        }

        initializeRecyclerView()

        callPostCommentsAPI()

        binding.ivSendComment.setOnClickListener {
            if (binding.etComment.text.trim().toString().isNullOrEmpty()){
                showCustomAlert("please enter comment.",binding.root)
                //Snackbar.makeText(this,"please enter comment.", Toast.LENGTH_SHORT).show()
            }
            else{
                val date = Calendar.getInstance().time
                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val strDate = dateFormat.format(date)
                addCommentAPI(binding.etComment.text.toString(), strDate)
                binding.etComment.setText("")
            }
        }
    }

//    private fun callPostCommentsAPI() {
//        viewModel.getPostComments(Common.post[0].postId.toString()).observe(this,{
//            if(it!=null){
//                commentAdapter!!.setData(it.data, viewModel)
//                commentAdapter!!.notifyDataSetChanged()
//            }
//        })
//    }



    private fun callPostCommentsAPI() {
        viewModel?.getPostComments(Common.post[0].postId.toString())?.observe(this) {
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    commentAdapter!!.setData(it.data!!.data, viewModel)
                    commentAdapter!!.notifyDataSetChanged()

                    binding.rvComment.scrollToPosition(it.data.data.size)
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun addCommentAPI(comment: String, strDate: String) {
        viewModel.addComment(comment,strDate,
            sharedPre().userId!!,
            Common.postIdForComment).observe(this, androidx.lifecycle.Observer {
            if(it!=null){
                if(it.code  == 400){
                    showCustomAlert("server error",binding.root)
                }
                else{
                   // initializeRecyclerView()
                    commentAdapter!!.setData(it.data, viewModel)
                    binding.scrollView.postDelayed(Runnable { binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 1000)
                }
                //Common.allComments.clear
            }
        })
    }

    private fun initializeRecyclerView() {
        commentAdapter = CommentAdapter(this)
        binding.rvComment.adapter = commentAdapter
        binding.rvComment.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvComment.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val fragment = getCurrentFragment()
//        if (fragment is CommentFragment) {
//            startActivityWithAnimation(
//                Intent(this,
//                    HomeActivity::class.java
//                ), Appconstants.SLIDE_IN_RIGHT
//            )
//        }

        if (Common.post[0].video != ""){
            exoPlayer!!.playWhenReady = false
            isPlay = false
        }
        finish()
        this.overridePendingTransition(  R.anim.slide_down,
            R.anim.slide_out_down)
    }

    fun getProxy(context: Context): HttpProxyCacheServer? {
        val app = context.applicationContext as common.neighbour.nearhud.application.MyApplication
        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(this)
    }

    override fun onLoading(isLoading: Boolean) {
        loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,binding.root)
    }

    override fun Logout() {

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