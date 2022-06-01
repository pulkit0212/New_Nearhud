package common.neighbour.nearhud.ui.setting

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.core.extensions.replaceFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.changed_frag.MypostFragment
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ActivityMyPostBinding
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.home.views.ExoPlayerManager
import common.neighbour.nearhud.ui.home.views.MuteStrategy
import common.neighbour.nearhud.ui.home.views.PlayStrategy
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.comment.CommentActivity
import common.neighbour.nearhud.ui.home.fragment.adapter.PostAdapter
import common.neighbour.nearhud.ui.home.fragment.create_post.CreatePostActivity
import common.neighbour.nearhud.ui.post.EditPostActivity

class MyPostActivity : NewBaseActivity<MainViewModel,ActivityMyPostBinding>() ,PostAdapter.OnVoteClick {

    override fun getViewBinding() = ActivityMyPostBinding.inflate(layoutInflater)
    lateinit var postAdapter: PostAdapter
    private var postData = mutableListOf<Data>()
    //private var postAdapter: PostAdapter? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding.viewModel = viewModel
        binding.activity = this
        initializeRecyclerView()
        WorkStation()
//        Handler().postDelayed({
//            (binding.rvPost.layoutManager as LinearLayoutManager).scrollToPositionWithOffset( Common.MY_POST_POSITION, 0)
//        }, 1000)
    }

    fun WorkStation() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.refresh.setOnRefreshListener {
            if (Appconstants.isMention){
                callMyPostAPI(Appconstants.USER_ID)
            }
            else{
                callMyPostAPI(sharedPre().userId!!)
            }
            binding.refresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        Observers()
    }

    private fun Observers() {
        // initializeRecyclerView()

        if (Appconstants.isMention){
            callMyPostAPI(Appconstants.USER_ID)
        }
        else{
            callMyPostAPI(sharedPre().userId!!)
        }
    }

    private fun callMyPostAPI(userId: String) {
        viewModel.getMyPost(userId).observe(this,{
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    Appconstants.POST_TYPE = 1
                    postData.clear()
                    postData.addAll(it.data!!.data)
                    postAdapter!!.setData(postData, viewModel, sharedPre().userId)
                    postAdapter!!.notifyDataSetChanged()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        postAdapter = PostAdapter(this,this,sharedPre())
        binding.rvPost.adapter = postAdapter
        binding.rvPost.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPost.layoutManager = layoutManager
        val exoPlayerManager = ExoPlayerManager(this,R.id.exoPlayer,true,
            PlayStrategy.DEFAULT,
            MuteStrategy.ALL,
            AppConstance.IS_MUTE,false,100,5)

        exoPlayerManager.attachToRecyclerView(binding.rvPost)
        exoPlayerManager.makeLifeCycleAware(this)

        binding.rvPost.smoothScrollToPosition(Common.MY_POST_POSITION  )
    }

    override fun onCommentClick(position: Int) {
        startActivityWithAnimation(
            Intent(this, CommentActivity::class.java), Appconstants.SLIDE_IN_TOP
        )
    }

    override fun onTagClick(userId: String,tag:String) {
        Appconstants.isMention = true
        Appconstants.USER_ID = userId
        if (supportFragmentManager.findFragmentById(R.id.container) !is MypostFragment){
            replaceFragment(MypostFragment(), R.id.container, "frag_mypost")
        }
        else{
            if (Appconstants.isMention){
                callMyPostAPI(Appconstants.USER_ID)
            }
            else{
                callMyPostAPI(sharedPre().userId!!)
            }
        }
    }

    override fun upVoteClick(position: Int, postId: Int, userId: String?, utext: TextView, dtext: TextView, uImage: ImageView, dImage: ImageView) {
        viewModel.UpVoteAPI(postId, userId!!).observe(this){
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    postData[position] = it.data!!.data[0]
                    if(it.data.data[0].upvoteId){
                        uImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.upvote))
                    }
                    else{
                        uImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.up))
                    }
                    if(it.data!!.data[0].downvoteId){
                        dImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.downvote))
                    }
                    else{
                        dImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.down))
                    }
                    utext.text = it.data!!.data[0].upvoteCount.toString()
                    dtext.text = it.data!!.data[0].downvoteCount.toString()
                }
            }
        }
    }

    override fun downVoteClick(position: Int, postId: Int, userId: String, dtext: TextView, utext: TextView, uImage: ImageView, dImage: ImageView) {
        viewModel.DownVoteAPI(postId, userId).observe(this){
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    postData[position] = it.data!!.data[0]
                    if(it.data.data[0].upvoteId){
                        uImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.upvote))
                    }
                    else{
                        uImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.up))
                    }
                    if(it.data!!.data[0].downvoteId){
                        dImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.downvote))
                    }
                    else{
                        dImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.down))
                    }
                    Log.d("DONE",it.data!!.data[0].downvoteCount.toString())
                    dtext.text = it.data!!.data[0].downvoteCount.toString()
                    utext.text = it.data!!.data[0].upvoteCount.toString()
                }
            }
        }
    }

    override fun onThreeDotClick(position: Int,data: Data) {
        val dialog = BottomSheetDialogs.getHomeThreeDotDialog(this)
        val clReport = dialog.findViewById<LinearLayout>(R.id.clReport)
        val clProfile = dialog.findViewById<LinearLayout>(R.id.clProfile)
        val clUpdate = dialog.findViewById<LinearLayout>(R.id.clUpdate)

        clReport.visibility = View.GONE
        clProfile.visibility = View.GONE
        clUpdate.visibility = View.VISIBLE

        clUpdate.setOnClickListener {
            Common.updatePostData = data
            if (data.image == "" && data.video == ""){
                Common.isPostUpdate = true
                val intent = Intent(this, CreatePostActivity::class.java)
                this.startActivity(intent)
                dialog.dismiss()
            }
            else{
                Common.isPostUpdate = true
                startActivity(Intent(this, EditPostActivity::class.java))
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}