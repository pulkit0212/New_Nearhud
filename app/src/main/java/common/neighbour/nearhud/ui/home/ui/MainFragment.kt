package common.neighbour.nearhud.ui.home.ui
import android.app.AlertDialog
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.core.extensions.replaceFragment
import com.bumptech.glide.Glide
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentMainBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.home.views.ExoPlayerManager
import common.neighbour.nearhud.ui.home.views.MuteStrategy
import common.neighbour.nearhud.ui.home.views.PlayStrategy
import common.neighbour.nearhud.repositories.constance.AppConstance.Companion.IS_MUTE
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.comment.CommentActivity
import common.neighbour.nearhud.ui.home.fragment.adapter.PostAdapter
import common.neighbour.nearhud.ui.home.fragment.create_post.CreatePostActivity
import common.neighbour.nearhud.changed_frag.MypostFragment
import common.neighbour.nearhud.common.Common
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest

class MainFragment : NewBaseFragment<MainViewModel,
        FragmentMainBinding>(MainViewModel::class.java),PostAdapter.OnVoteClick
    {

    override fun getLayoutRes() = R.layout.fragment_main
    var cvNeighbour: CardView? = null
    var cvHome: CardView? = null
    var ivCross: ImageView? = null
    lateinit var postAdapter: PostAdapter
    private var postData = mutableListOf<Data>()

    private val loader by lazy { ProgressView.getLoader(requireActivity()) }

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        Common.isPostMediaChange = false

        if(isNetworkConnected()){
            binding.internerError.visibility = View.GONE
            getProfile()
        }
        else{
            binding.internerError.visibility = View.VISIBLE
        }


        initializeRecyclerView()
        WorkStation()

    }

        private fun getProfile() {
        viewModel.GetProfile(getSharedPre().userId!!).observe(requireActivity(),{

            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    binding.toolbar.tvGroupName.text = it.data!!.data[0].name +" , "+it.data!!.data[0].city
                    if(!it.data.data[0].profilePicture.isNullOrEmpty()){
                        Glide.with(requireActivity()).load(it.data.data[0].profilePicture).placeholder(R.drawable.user).into(binding.toolbar.ivProfileImage)
                        getSharedPre().setEmailProfile(it.data.data[0].profilePicture)
                    }else{
                        Glide.with(requireActivity()).load(R.drawable.user).placeholder(R.drawable.user).into(binding.toolbar.ivProfileImage)
                    }
                    if(!it.data.data[0].profilePicture.isNullOrEmpty()){
                        Glide.with(requireActivity()).load(it.data.data[0].profilePicture).placeholder(R.drawable.user).into(binding.ivProfileImage)
                    }else{
                        Glide.with(requireActivity()).load(R.drawable.user).placeholder(R.drawable.user).into(binding.ivProfileImage)
                    }

                    getSharedPre().setUserFName(it.data.data[0].firstName)
                    getSharedPre().setUserLName(it.data.data[0].lastName)
                    getSharedPre().setUserNumber(it.data.data[0].phoneNo)
                    getSharedPre().setUserEmail(it.data.data[0].email)
                    getSharedPre().setUserGroupName(it.data.data[0].name)
                    getSharedPre().setUserCity(it.data.data[0].city)
                    getSharedPre().setUserState(it.data.data[0].state)
                    getSharedPre().setUserGroupId(it.data.data[0].groupId)
                    getSharedPre().setUserAddress(it.data.data[0].address)
                    getSharedPre().setUserOccupation(it.data.data[0].occupation)
                    getSharedPre().setUserZip(it.data.data[0].zip)
                    getSharedPre().setUserLatlng(it.data.data[0].latLong)
                    getSharedPre().setUserCreateDate(it.data.data[0].createdOn)

                    getHomeData()
                }
            }
        })
    }

        private fun getHomeData(){
            when (getSharedPre().userChoice) {
                "home" -> {
                    callPostStateAPI(true)
                }
                "neighbour" -> {
                    callPostStateAPI(false)
                }
                else -> {
                    callPostStateAPI(true)
                }
            }
        }

    fun WorkStation() {
        binding.refresh.setOnRefreshListener {
            initializeRecyclerView()
            getHomeData()
             binding.refresh.isRefreshing = false
        }
        binding.homeTextPost.setOnClickListener {
            Common.isPostUpdate = false
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            requireContext().startActivity(intent)
        }

        binding.toolbar.ivFilter.setOnClickListener {
            //showFilterDialog()

            showNewFilterDialog()
        }
        binding.toolbar.ivProfileImage.setOnClickListener {
            viewModel.switchSettingPage2(requireActivity())
        }
    }

        private fun showNewFilterDialog() {
            val dialog = BottomSheetDialogs.getHomeFilterDialog(requireContext())
            val clHome = dialog.findViewById<LinearLayout>(R.id.clHome)
            val clNeighbour = dialog.findViewById<LinearLayout>(R.id.clNeighbour)
            clHome.setOnClickListener {
                getSharedPre().setUserChoice("home")
                callPostStateAPI(true)
                dialog.dismiss()
            }
            clNeighbour.setOnClickListener {
                getSharedPre().setUserChoice("neighbour")
                callPostStateAPI(false)
                // callPostStateAPI()
                dialog.dismiss()
            }
            dialog.show()
        }

//

    private fun callPostStateAPI(isState:Boolean) {
//        loader.show()
//        newPostAdapter = null
//        newPostAdapter = PagingPostAdapter(this,viewModel,getSharedPre())
//        binding.rvPost.adapter = newPostAdapter!!.withLoadStateFooter( footer = LoadStateFooterAdapter())
//        lifecycleScope.launch {
//            viewModel.getPosts(isState,
//                getSharedPre().userGroupId!!,
//                getSharedPre().userId!!).collectLatest {
//                loader.dismiss()
//                newPostAdapter!!.submitData(it)
//            }
//        }

//        viewModel.getHomePosts(isState,
//            getSharedPre().userGroupId!!,
//            getSharedPre().userId!!).observe(this, Observer {
//            loader.dismiss()
//            newPostAdapter!!.submitData(lifecycle,it)
//        })

        viewModel?.getPostByState(
            getSharedPre().userGroupId!!,
            getSharedPre().userId!!,
            isState)?.observe(viewLifecycleOwner) {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show ()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    if (it.data!!.data.size == 0){
                        binding.homeTextPost.visibility = View.VISIBLE
                        binding.noPostTag.visibility = View.VISIBLE
                        binding.refresh.visibility = View.GONE
                    }
                    else{
                        binding.homeTextPost.visibility = View.GONE
                        binding.noPostTag.visibility = View.GONE
                        binding.refresh.visibility = View.VISIBLE
                        Appconstants.POST_TYPE = 0
                        postData.clear()
                        postData.addAll(it.data!!.data)
                        postAdapter!!.setData(postData, viewModel,getSharedPre().userId)
                        binding.rvPost.scrollToPosition(0)
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(requireContext(),it.data!!.message,Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun showFilterDialog() {
        val alertDialog = AlertDialog.Builder(activity)
        val inflater = this.layoutInflater
        val view: View = inflater.inflate(R.layout.home_dialog, null)

        cvNeighbour = view.findViewById<View>(R.id.cv_neighbour) as CardView
        cvHome = view.findViewById<View>(R.id.cv_home) as CardView
        ivCross = view.findViewById<View>(R.id.iv_cross) as ImageView
        alertDialog.setView(ivCross)
        val dialog = alertDialog.create()


        //  AlertDialog.Builder show = alertDialog.show();
        cvNeighbour!!.setOnClickListener(View.OnClickListener {
            getSharedPre().setUserChoice("neighbour")
            callPostStateAPI(false)
            // callPostStateAPI()
            dialog.dismiss()
        })
        cvHome!!.setOnClickListener(View.OnClickListener {
            getSharedPre().setUserChoice("home")
            callPostStateAPI(true)
            dialog.dismiss()
        })
        ivCross!!.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            //  show.dismiss();
        })
        dialog.setView(view)
        dialog.show()
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
            IS_MUTE,false,100,5)

        exoPlayerManager.attachToRecyclerView(binding.rvPost)
        exoPlayerManager.makeLifeCycleAware(this)
       // newPostAdapter = PagingPostAdapter(this,viewModel,getSharedPre())
       // binding.rvPost.adapter = newPostAdapter!!.withLoadStateFooter( footer = LoadStateFooterAdapter())

    }

        override fun upVoteClick(
            position: Int,
            postId: Int,
            userId: String?,
            utext: TextView,
            dtext: TextView,
            uImage: ImageView,
            dImage: ImageView
        ) {
            viewModel.UpVoteAPI(postId,getSharedPre().userId!!).observe(viewLifecycleOwner){
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

        override fun onResume() {
            super.onResume()
            //checkNetwork()
            Common.isPostUpdate = false
            Common.isPostMediaChange = false
        }

        private fun checkNetwork() {
            lifecycleScope.launchWhenResumed {
                networkAvailableFlow().collectLatest { networkIsAvailable ->
                    println(networkIsAvailable)
                    if(networkIsAvailable){
                        binding.internerError.visibility = View.GONE
                        //getProfile()
                    }else{
                        binding.internerError.visibility = View.VISIBLE
                    }
                }
            }
        }

        @ExperimentalCoroutinesApi
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun networkAvailableFlow(): Flow<Boolean> = callbackFlow {
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        offer(true)
                    }

                    override fun onLost(network: Network) {
                        offer(false)
                    }

                    override fun onUnavailable() {
                        offer(false)
                    }
                }
            val manager = requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.registerNetworkCallback(NetworkRequest.Builder().run {
                addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                build()
            }, callback)
            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }

        override fun downVoteClick(
            position: Int,
            postId: Int,
            userId: String,
            dtext: TextView,
            utext: TextView,
            uImage: ImageView,
            dImage: ImageView
        ) {
            viewModel.DownVoteAPI(postId, getSharedPre().userId!!).observe(viewLifecycleOwner){
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

        override fun onCommentClick(position: Int) {
            startActivityWithAnimation(
                Intent(context, CommentActivity::class.java), Appconstants.SLIDE_IN_TOP
            )
        }


        override fun onThreeDotClick(position: Int,data: Data) {
            activity?.let {
                var dialog = BottomSheetDialogs.getHomeThreeDotDialog(it)
                val clReport = dialog.findViewById<LinearLayout>(R.id.clReport)
                val clProfile = dialog.findViewById<LinearLayout>(R.id.clProfile)
                val clUpdate = dialog.findViewById<LinearLayout>(R.id.clUpdate)

                clReport.visibility = View.VISIBLE
                clProfile.visibility = View.VISIBLE
                clUpdate.visibility = View.GONE

                clReport.setOnClickListener {
//                    startActivity(Intent(activity, ReportActivity::class.java))
//                    dialog.dismiss()
                }
                clProfile.setOnClickListener {
//                    startActivity(Intent(activity, AppointmentActivity::class.java)
//                        .putExtra("receive_user_id",postHome.user_id)
//                        .putExtra("isUpdate","0"))
//                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        override fun onTagClick(userId: String,tag:String) {
        //if (tag[0] == "#")
        Appconstants.isMention = true
        Appconstants.USER_ID = userId
        if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is MypostFragment){
            requireActivity().replaceFragment(MypostFragment(), R.id.container, "frag_mypost")
        }
    }

        private fun isNetworkConnected(): Boolean {
            val cm = requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }
}