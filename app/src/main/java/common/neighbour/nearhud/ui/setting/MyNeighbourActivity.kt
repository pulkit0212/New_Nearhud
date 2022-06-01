package common.neighbour.nearhud.ui.setting

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ActivityMyNeighbourBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.setting.adapter.NeighbourAdapter

class MyNeighbourActivity  : NewBaseActivity<MainViewModel, ActivityMyNeighbourBinding>() {

    override fun getViewBinding() = ActivityMyNeighbourBinding.inflate(layoutInflater)
    private var neighbourAdapter: NeighbourAdapter? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeRecyclerView()
        WorkStation()
    }
    fun WorkStation() {
        callMyNeighbourAPI()
        initializeRecyclerView()
        binding.ivBack.setOnClickListener {
           finish()
        }
        binding.refresh.setOnRefreshListener {
            callMyNeighbourAPI()
            binding.refresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        callMyNeighbourAPI()
    }

    private fun callMyNeighbourAPI() {
        viewModel?.getMyNeighbour(
            sharedPre().userId!!)?.observe(this) {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    neighbourAdapter!!.setData(it.data!!.data, viewModel!!)
                    neighbourAdapter!!.notifyDataSetChanged()
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
        neighbourAdapter = NeighbourAdapter(this)
        binding.rvPost.adapter = neighbourAdapter
        binding.rvPost.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPost.layoutManager = layoutManager
    }
}