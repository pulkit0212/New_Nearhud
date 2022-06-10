package common.neighbour.nearhud.ui.home.fragment.setting

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import common.neighbour.nearhud.core.extensions.replaceFragment
import com.bumptech.glide.Glide
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.LogoutDialogBinding
import common.neighbour.nearhud.databinding.SettingFragmentBinding
import common.neighbour.nearhud.module.Utility
import common.neighbour.nearhud.ui.login.LoginActivity
import common.neighbour.nearhud.changed_frag.HelpFragment
import common.neighbour.nearhud.changed_frag.MyNeighbourFragment
import com.google.android.material.tabs.TabLayout
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.changed_frag.MypostFragment
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.contact_share.ContactActivity
import common.neighbour.nearhud.ui.home.fragment.setting.profile.ProfileActivity
import common.neighbour.nearhud.ui.setting.HelpActivity
import common.neighbour.nearhud.ui.setting.MyNeighbourActivity
import common.neighbour.nearhud.ui.setting.MyPostActivity
//import com.google.android.material.tabs.TabLayoutMediator
import common.neighbour.nearhud.ui.setting.adapter.ViewPagerProfileAdapter


class SettingFragment : NewBaseFragment<MainViewModel, SettingFragmentBinding>(MainViewModel::class.java) {

    override fun getLayoutRes() = R.layout.setting_fragment
    private var dialogBinding: LogoutDialogBinding? = null
    private var logoutDialog: Dialog? = null
    private val repo = MyApplication.getInstance()!!.getNetworkRepo()

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        WorkStation()
    }

    fun WorkStation() {
       // getDialogLogout()

        /**Set Category Recycler View*/
       // setupTabIcons()

//        binding.rvCategory.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//
//        binding.rvCategory.adapter = ViewPagerProfileAdapter(
//            getSharedPre().userId!!, lifecycle,requireContext())
//        TabLayoutMediator(binding.tabLayout, binding.rvCategory) { tab, position ->
//            if (position == 0) {
//                tab!!.customView = Utility.getSelectedCustomTab(requireActivity(), position)
//            } else {
//                tab!!.customView = Utility.getCustomTab(requireActivity(), position)
//            }
//        }.attach()
    }

    private fun setupTabIcons() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                binding.tabLayout.getTabAt(tab!!.position)!!.customView = null
                binding.tabLayout.getTabAt(tab!!.position)!!
                    .setCustomView(Utility.getCustomTab(requireActivity(), tab!!.position))
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.tabLayout.getTabAt(tab!!.position)!!.customView = null
                binding.tabLayout.getTabAt(tab!!.position)!!.customView =
                    Utility.getSelectedCustomTab(requireActivity(), tab!!.position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Observers()
        AccessClickLIstener()
    }

    private fun AccessClickLIstener() {

        binding.frameLogout.setOnClickListener {
            val dialog = BottomSheetDialogs.getLogoutDialog(requireContext())
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnLogout.setOnClickListener {
                val getToken: String = GetSharedPre().jwtToken!!
                GetSharedPre().Logout()
                //FirebaseAuth.getInstance().signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
                requireActivity().finish()
            }
            dialog.show()
        }


        binding.frameLayout5.setOnClickListener {
            val intent = Intent(requireContext(), MyPostActivity::class.java)
            requireContext().startActivity(intent)
        }

        binding.frameLayout6.setOnClickListener {
            val intent = Intent(requireContext(), MyNeighbourActivity::class.java)
            requireContext().startActivity(intent)
        }
        binding.frameLayout4.setOnClickListener {
            val intent = Intent(requireContext(), HelpActivity::class.java)
            requireContext().startActivity(intent)
        }
        binding.frameLayout3.setOnClickListener {
            val intent = Intent(requireContext(), HelpActivity::class.java)
            requireContext().startActivity(intent)
        }
        binding.frameShare.setOnClickListener {
            val intent = Intent(requireContext(), ContactActivity::class.java)
            requireContext().startActivity(intent)
        }

//        binding.frameLayout5.setOnClickListener {
//            if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is MypostFragment){
//                requireActivity().replaceFragment(MypostFragment(), R.id.container, "frag_mypost")
//            }
//        }
//        binding.frameLayout6.setOnClickListener {
//            if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is MyNeighbourFragment){
//                requireActivity().replaceFragment(MyNeighbourFragment(), R.id.container, "frag_myneigh")
//            }
//        }
//        binding.frameLayout4.setOnClickListener {
//            if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is HelpFragment){
//                requireActivity().replaceFragment(HelpFragment(), R.id.container, "frag_help")
//            }
//        }
        binding.ivEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            requireContext().startActivity(intent)
        }
        binding.ivSetting.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            requireContext().startActivity(intent)
        }

    }
    private fun Observers() {
        viewModel!!.GetProfile(getSharedPre().userId!!).observe(requireActivity()) {
            if (it?.data != null) {
                binding.tvName.text = it.data.data[0].firstName + " " + it.data.data[0].lastName
                binding.tvGrpname.text = it.data.data[0].name
                binding.tvEmail.text = it.data.data[0].phoneNo
                if (!it.data.data[0].profilePicture.isNullOrEmpty()) {
                    Glide.with(requireActivity()).load(it.data.data[0].profilePicture)
                        .placeholder(R.drawable.user).into(binding.circleImageView)
                    // sharedPre.setEmailProfile(it.data[0].profilePicture)
                } else {
                    Glide.with(requireActivity()).load(R.drawable.user).placeholder(R.drawable.user)
                        .into(binding.circleImageView)
                }
            }
        }
    }

    private fun getDialogLogout() {
        logoutDialog = Dialog(requireActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.logout_dialog,
            null,
            false
        )
        logoutDialog!!.setContentView(dialogBinding!!.root)
        dialogBinding!!.logoutBtn.setOnClickListener {
            val getToken: String = GetSharedPre().jwtToken!!
            GetSharedPre().Logout()
            //FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            requireActivity().finish()
        }
        dialogBinding!!.cancelBtn.setOnClickListener { logoutDialog!!.dismiss() }
    }

}