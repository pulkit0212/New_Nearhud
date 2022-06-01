package common.neighbour.nearhud.ui.welcome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import common.neighbour.nearhud.ui.welcome.fragments.WelcomeFragment1
import common.neighbour.nearhud.ui.welcome.fragments.WelcomeFragment2
import common.neighbour.nearhud.ui.welcome.fragments.WelcomeFragment3
import common.neighbour.nearhud.ui.welcome.viewModel.WelcomeViewModel

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WelcomeFragment1()
            1 -> return WelcomeFragment2()
            2 -> return WelcomeFragment3()
            else -> {
                return WelcomeFragment1()
            }
        }
    }

}