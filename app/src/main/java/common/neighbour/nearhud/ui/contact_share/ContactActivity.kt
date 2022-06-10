package common.neighbour.nearhud.ui.contact_share

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.databinding.ActivityContactBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.retrofit.model.contact_share.ContactList
import common.neighbour.nearhud.ui.contact_share.adapter.ContactAdapter
import common.neighbour.nearhud.ui.contact_share.adapter.OnSendInterface
import common.neighbour.nearhud.ui.contact_share.adapter.TabContactShareAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactActivity : NewBaseActivity<ContactViewModel, ActivityContactBinding>()  {

    override fun getViewBinding() = ActivityContactBinding.inflate(layoutInflater)

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTabListener()
        binding.backBtnToolbar.setOnClickListener {
            finish()
            //9425315142
        }
    }


    private fun initTabListener() {
        binding.apply {
            tabLayout=tabs
            tabs.addTab(tabs.newTab().setText("UnInvited"))
            tabs.addTab(tabs.newTab().setText("Invited"))


            (tabs as TabLayout).setBackgroundColor(ContextCompat.getColor(this@ContactActivity, R.color.white))
            (tabs as TabLayout).setSelectedTabIndicatorColor(
                ContextCompat.getColor(
                    this@ContactActivity,
                    R.color.colorPrimary))

            (tabs as TabLayout).setTabTextColors(
                ContextCompat.getColor(this@ContactActivity, R.color.grey),
                ContextCompat.getColor(this@ContactActivity, R.color.black))


            val adapter = this.let {
                TabContactShareAdapter(
                    this@ContactActivity,
                    supportFragmentManager,
                    tabs.tabCount
                )
            }

            val pos= 0
            viewpager.adapter = adapter

            viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

            viewpager.setCurrentItem(pos)
            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewpager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })
        }

    }
}