package common.neighbour.nearhud.ui.contact_share.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import common.neighbour.nearhud.ui.contact_share.InvitedFragment
import common.neighbour.nearhud.ui.contact_share.UnInvitedFragment

internal class TabContactShareAdapter (
    var context: Context, fm: FragmentManager, internal var totalTabs: Int
) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                UnInvitedFragment().newInstance()
            }
            1 -> {
                InvitedFragment().newInstance()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
