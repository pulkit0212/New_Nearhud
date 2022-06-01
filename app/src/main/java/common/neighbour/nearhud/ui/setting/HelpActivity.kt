package common.neighbour.nearhud.ui.setting

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ActivityHelpBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.setting.adapter.HelpAdapter

class HelpActivity : NewBaseActivity<MainViewModel, ActivityHelpBinding>()  {

    override fun getViewBinding() = ActivityHelpBinding.inflate(layoutInflater)
    private var helpAdapter: HelpAdapter? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeRecyclerView()
        WorkStation()
    }
    fun WorkStation() {
        callHelpAPI()
        initializeRecyclerView()
        binding.ivBack.setOnClickListener {
           finish()
        }
        binding.refresh.setOnRefreshListener {
            callHelpAPI()
            binding.refresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        callHelpAPI()
    }

    private fun callHelpAPI() {
        viewModel?.getHelp()?.observe(this) {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    helpAdapter!!.setData(it.data!!.data, viewModel!!)
                    helpAdapter!!.notifyDataSetChanged()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        helpAdapter = HelpAdapter(this)
        binding.rvHelp.adapter = helpAdapter
        binding.rvHelp.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHelp.layoutManager = layoutManager
    }
}