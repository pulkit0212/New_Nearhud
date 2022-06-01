package common.neighbour.nearhud.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import common.neighbour.nearhud.base.BaseFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.FragmentCommentBinding
import common.neighbour.nearhud.ui.home.fragment.adapter.CommentAdapter
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CommentFragment : BaseFragment(), HomeNavigator {
    private lateinit var binding: FragmentCommentBinding
    private var commentAdapter: CommentAdapter? = null

    companion object {
        private var instance: CommentFragment? = null
        private var viewModel: HomeViewModel? = null
        @JvmStatic
        fun newInstance(viewModelm: HomeViewModel): CommentFragment? {
            this.viewModel=viewModelm
            if (instance == null) {
                instance = CommentFragment()
            }
            return instance
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_comment,
            container,
            false
        )
        viewModel!!.navigator=this
        WorkStation()
        return binding.root
    }

    override fun WorkStation() {

        initializeRecyclerView()
      //  commentAdapter!!.setData(common.neighbour.nearhud.common.Common.allComments, viewModel!!)

        binding.ivSendComment.setOnClickListener {
            val date = Calendar.getInstance().time
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val strDate = dateFormat.format(date)
            addCommentAPI(binding.etComment.getText().toString(), strDate)
            binding.etComment.setText("")
        }
    }

    private fun addCommentAPI(comment: String, strDate: String) {
        viewModel!!.addComment(comment,strDate,
            Common.userIdForComment,
            Common.postIdForComment).observe(requireActivity(),Observer{
            if(it!=null){
                Common.allComments.clear()
                //Common.allComments = it.data[0].comment
                //commentAdapter!!.setData(Common.allComments, viewModel!!)
            }
        })

    }

    private fun initializeRecyclerView() {
        commentAdapter = CommentAdapter(requireActivity())
        binding.rvComment.setAdapter(commentAdapter)
        binding.rvComment.setNestedScrollingEnabled(false)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvComment.setLayoutManager(layoutManager)
    }

    override fun onResume() {
        super.onResume()
        Observers()
    }

    private fun Observers() {
//        viewModel!!.getStatePost("rajasthan","u123","1").observe(requireActivity(),{
//            if(it!=null){
//                commentAdapter!!.setData(it.data, viewModel!!)
//            }
//        })
    }

    override fun onLoading(isLoading: Boolean) {
        getBaseActivity()!!.loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,binding.root)
    }

    override fun Logout() {

    }

}