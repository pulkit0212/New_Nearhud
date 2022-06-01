package common.neighbour.nearhud.ui.comment

import android.os.Bundle
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityReplyBinding
import common.neighbour.nearhud.ui.home.fragment.adapter.ReplyAdapter
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ReplyActivity : BaseActivity(), HomeNavigator {
    private lateinit var binding: ActivityReplyBinding
    private val viewModel: HomeViewModel by viewModels()
    private var replyAdapter: ReplyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_reply)

        viewModel.navigator=this
        WorkStation()
        binding.scroll.postDelayed(Runnable { binding.scroll.fullScroll(ScrollView.FOCUS_DOWN) }, 1000)
    }

    private fun WorkStation() {

        binding.ivBack.setOnClickListener {
            finish()
            this.overridePendingTransition(  R.anim.slide_down,
                R.anim.slide_out_down)
        }

        if(sharedPre().emailProfile!=""){
            Glide.with(this).load(sharedPre().emailProfile).placeholder(R.drawable.user).into(binding.ivProfileComment)
        }else{
            Glide.with(this).load(R.drawable.user).placeholder(R.drawable.user).into(binding.ivProfileComment)
        }
        binding.replyTag.text = Common.commentText

        initializeRecyclerView()

        callCommentReplyAPI()

        binding.ivSendComment.setOnClickListener {
            if (binding.etComment.text.trim().toString().isNullOrEmpty()){
                showCustomAlert("please enter comment.",binding.root)
                //Toast.makeText(this,"please enter comment.", Toast.LENGTH_SHORT).show()
            }
            else{
                val date = Calendar.getInstance().time
                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val strDate = dateFormat.format(date)
                addReplyAPI(binding.etComment.text.toString(), strDate)
                binding.etComment.setText("")
            }
        }
    }

    private fun addReplyAPI(reply: String, strDate: String) {
        viewModel.addReply(reply,strDate, sharedPre().userId!!,
            Common.postIdForComment,Common.commentIdForReply).observe(this, androidx.lifecycle.Observer {
            if(it!=null){
                if(it.code  == 400){
                    showCustomAlert("server error",binding.root)
                }
                else{
                   // initializeRecyclerView()
                    replyAdapter!!.setData(it.data, viewModel)
                    replyAdapter!!.notifyDataSetChanged()
                    binding.scroll.postDelayed(Runnable { binding.scroll.fullScroll(ScrollView.FOCUS_DOWN) }, 1000)
                }
                //Common.allComments.clear
            }
        })
    }
//
//    private fun callCommentReplyAPI() {
//        viewModel.getCommentReply(Common.commentIdForReply).observe(this,{
//            if(it!=null){
//                replyAdapter!!.setData(it.data, viewModel)
//                replyAdapter!!.notifyDataSetChanged()
//            }
//        })
//    }
    private fun callCommentReplyAPI() {
        viewModel?.getCommentReply(Common.commentIdForReply)?.observe(this) {
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    replyAdapter!!.setData(it.data!!.data, viewModel)
                    replyAdapter!!.notifyDataSetChanged()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        replyAdapter = ReplyAdapter(this)
        binding.rvReplyComment.adapter = replyAdapter
        binding.rvReplyComment.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvReplyComment.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        this.overridePendingTransition(  R.anim.slide_down,
            R.anim.slide_out_down)
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