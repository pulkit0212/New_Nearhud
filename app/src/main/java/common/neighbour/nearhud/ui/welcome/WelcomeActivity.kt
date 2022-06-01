package common.neighbour.nearhud.ui.welcome

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityWelcomeBinding
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.welcome.adapter.ViewPagerFragmentAdapter
import common.neighbour.nearhud.ui.welcome.viewModel.WelcomeViewModel

class WelcomeActivity : BaseActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var adapter: ViewPagerFragmentAdapter
    private val viewModel: WelcomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        viewModel.getLaunchScreen().observe(this, Observer {
            when (it.status) {
                BaseDataSource.Resource.Status.SUCCESS -> {
                    Common.launchData.addAll(it.data!!.data)

                    adapter= ViewPagerFragmentAdapter(this)
                    binding.welcomePager.adapter=adapter
                    binding.dotsIndicator.setViewPager2(binding.welcomePager)
                }
            }
        })
    }
}
