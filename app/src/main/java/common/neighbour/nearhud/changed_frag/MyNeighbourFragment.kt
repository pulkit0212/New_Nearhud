package common.neighbour.nearhud.changed_frag

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentMyNeighbourBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.setting.adapter.NeighbourAdapter

class MyNeighbourFragment : NewBaseFragment<MainViewModel, FragmentMyNeighbourBinding>(MainViewModel::class.java) {

    override fun getLayoutRes() = R.layout.fragment_my_neighbour
    private var neighbourAdapter: NeighbourAdapter? = null
    private val loader by lazy { ProgressView.getLoader(requireActivity()) }

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeRecyclerView()
        WorkStation()
    }

    fun WorkStation() {
        callMyNeighbourAPI()
        initializeRecyclerView()
        binding.ivBack.setOnClickListener {
            viewModel.switchSettingPage2(requireActivity())
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
            getSharedPre().userId!!)?.observe(viewLifecycleOwner) {
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
                    Toast.makeText(requireContext(),it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        neighbourAdapter = NeighbourAdapter(requireActivity())
        binding.rvPost.adapter = neighbourAdapter
        binding.rvPost.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPost.layoutManager = layoutManager
    }

}