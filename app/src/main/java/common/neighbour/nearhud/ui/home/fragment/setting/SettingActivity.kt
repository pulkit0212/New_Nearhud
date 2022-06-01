package common.neighbour.nearhud.ui.home.fragment.setting

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.ActivitySettingBinding
import common.neighbour.nearhud.databinding.LogoutDialogBinding
import common.neighbour.nearhud.ui.login.LoginActivity
import common.neighbour.nearhud.ui.setting.HelpActivity
import common.neighbour.nearhud.ui.setting.MyNeighbourActivity

class SettingActivity : NewBaseActivity<MainViewModel, ActivitySettingBinding>()  {

    override fun getViewBinding() = ActivitySettingBinding.inflate(layoutInflater)
    private var dialogBinding: LogoutDialogBinding? = null
    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        AccessClickLIstener()
    }

    private fun AccessClickLIstener() {

        binding.frameLogout.setOnClickListener {
            val dialog = BottomSheetDialogs.getLogoutDialog(this)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnLogout.setOnClickListener {
                val getToken: String = sharedPre().jwtToken!!
                sharedPre().Logout()
                //FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            dialog.show()
        }

        binding.frameLayout6.setOnClickListener {
            val intent = Intent(this, MyNeighbourActivity::class.java)
            this.startActivity(intent)
        }
        binding.frameLayout4.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            this.startActivity(intent)
        }
        binding.frameLayout3.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            this.startActivity(intent)
        }

    }
}