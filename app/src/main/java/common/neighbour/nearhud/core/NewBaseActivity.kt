package common.neighbour.nearhud.core

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.database.prefrence.SharedPre
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

abstract class NewBaseActivity<VM : ViewModel, VB : ViewDataBinding> : AppCompatActivity() {
    lateinit var viewModel: VM
    lateinit var binding: VB
    lateinit var snackBar: Snackbar
//    @Inject
//    lateinit var sharedPre: SharedPre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        binding = getViewBinding()
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }

    fun sharedPre():SharedPre{
        return SharedPre.getInstance(this)!!
    }

    private fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return type as Class<VM>
    }

    open fun startActivityWithAnimation(intent: Intent?, animationType: Int) {
        startActivity(intent)
        when (animationType) {
            Appconstants.SLIDE_IN_RIGHT -> overridePendingTransition(
                R.anim.slide_right,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_BOTTOM -> overridePendingTransition(
                R.anim.slide_down,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_LEFT -> overridePendingTransition(
                R.anim.slide_left,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_TOP -> overridePendingTransition(
                R.anim.slide_up,
                R.anim.slide_out_up
            )
            Appconstants.SCALE -> overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
        }
    }


    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }
    open fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    open fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    abstract fun getViewBinding(): VB

}