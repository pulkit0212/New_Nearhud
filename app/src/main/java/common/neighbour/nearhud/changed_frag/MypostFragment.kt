package common.neighbour.nearhud.changed_frag

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.core.extensions.replaceFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentMypostBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.home.views.ExoPlayerManager
import common.neighbour.nearhud.ui.home.views.MuteStrategy
import common.neighbour.nearhud.ui.home.views.PlayStrategy
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.comment.CommentActivity
import common.neighbour.nearhud.ui.home.fragment.adapter.PostAdapter

class MypostFragment : NewBaseFragment<MainViewModel, FragmentMypostBinding>(MainViewModel::class.java)
    ,PostAdapter.OnVoteClick{

    override fun getLayoutRes() = R.layout.fragment_mypost
    lateinit var postAdapter: PostAdapter
    private var postData = mutableListOf<Data>()
    //private var postAdapter: PostAdapter? = null
    private val loader by lazy { ProgressView.getLoader(requireActivity()) }

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeRecyclerView()
        WorkStation()
    }


    fun WorkStation() {

        binding.ivBack.setOnClickListener {
            viewModel.switchSettingPage2(requireActivity())
        }

        binding.refresh.setOnRefreshListener {
            if (Appconstants.isMention){
                callMyPostAPI(Appconstants.USER_ID)
            }
            else{
                callMyPostAPI(getSharedPre().userId!!)
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
            callMyPostAPI(getSharedPre().userId!!)
        }
    }

    private fun callMyPostAPI(userId: String) {
        viewModel.getMyPost(userId).observe(requireActivity(),{
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    Appconstants.POST_TYPE = 1
                    postData.clear()
                    postData.addAll(it.data!!.data)
                    postAdapter!!.setData(postData, viewModel, getSharedPre().userId)
                    postAdapter!!.notifyDataSetChanged()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(requireContext(),it.data!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        postAdapter = PostAdapter(requireActivity(),this,getSharedPre())
        binding.rvPost.adapter = postAdapter
        binding.rvPost.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPost.layoutManager = layoutManager
        val exoPlayerManager = ExoPlayerManager(requireContext(),R.id.exoPlayer,true,
            PlayStrategy.DEFAULT,
            MuteStrategy.ALL,
            AppConstance.IS_MUTE,false,100,5)

        exoPlayerManager.attachToRecyclerView(binding.rvPost)
        exoPlayerManager.makeLifeCycleAware(this)
    }

    override fun onCommentClick(position: Int) {
        startActivityWithAnimation(
            Intent(context, CommentActivity::class.java), Appconstants.SLIDE_IN_TOP
        )
    }

    override fun onTagClick(userId: String,tag:String) {
        Appconstants.isMention = true
        Appconstants.USER_ID = userId
        if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is MypostFragment){
            requireActivity().replaceFragment(MypostFragment(), R.id.container, "frag_mypost")
        }
        else{
            if (Appconstants.isMention){
                callMyPostAPI(Appconstants.USER_ID)
            }
            else{
                callMyPostAPI(getSharedPre().userId!!)
            }
        }
    }

    override fun upVoteClick(position: Int,postId: Int, userId: String?, utext: TextView, dtext: TextView, uImage: ImageView, dImage: ImageView) {
        viewModel.UpVoteAPI(postId, userId!!).observe(viewLifecycleOwner){
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    postData[position] = it.data!!.data[0]
                    if(it.data.data[0].upvoteId){
                        uImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.upvote))
                    }
                    else{
                        uImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.up))
                    }
                    if(it.data!!.data[0].downvoteId){
                        dImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.downvote))
                    }
                    else{
                        dImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.down))
                    }
                    utext.text = it.data!!.data[0].upvoteCount.toString()
                    dtext.text = it.data!!.data[0].downvoteCount.toString()
                }
            }
        }
    }

    override fun downVoteClick(position: Int,postId: Int, userId: String, dtext: TextView, utext: TextView, uImage: ImageView, dImage: ImageView) {
        viewModel.DownVoteAPI(postId, userId).observe(viewLifecycleOwner){
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    postData[position] = it.data!!.data[0]
                    if(it.data.data[0].upvoteId){
                        uImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.upvote))
                    }
                    else{
                        uImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.up))
                    }
                    if(it.data!!.data[0].downvoteId){
                        dImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.downvote))
                    }
                    else{
                        dImage.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.down))
                    }
                    Log.d("DONE",it.data!!.data[0].downvoteCount.toString())
                    dtext.text = it.data!!.data[0].downvoteCount.toString()
                    utext.text = it.data!!.data[0].upvoteCount.toString()
                }
            }
        }
    }

}