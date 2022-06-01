package common.neighbour.nearhud.changed_frag

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentHelpBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.setting.adapter.HelpAdapter


class HelpFragment : NewBaseFragment<MainViewModel, FragmentHelpBinding>(MainViewModel::class.java) {

    override fun getLayoutRes() = R.layout.fragment_help
    private var helpAdapter: HelpAdapter? = null
    private val loader by lazy { ProgressView.getLoader(requireActivity()) }

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeRecyclerView()
        WorkStation()
    }

    fun WorkStation() {
        callHelpAPI()
        initializeRecyclerView()
        binding.ivBack.setOnClickListener {
            viewModel.switchSettingPage2(requireActivity())
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
        viewModel?.getHelp()?.observe(viewLifecycleOwner) {
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
                    Toast.makeText(requireContext(),it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        helpAdapter = HelpAdapter(requireActivity())
        binding.rvHelp.adapter = helpAdapter
        binding.rvHelp.isNestedScrollingEnabled = false
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvHelp.layoutManager = layoutManager
    }

}