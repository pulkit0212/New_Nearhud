package common.neighbour.nearhud.core

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.database.prefrence.SharedPre

abstract class NewBaseFragment<VM : BaseViewModel, DB : ViewDataBinding>(private val mViewModelClass: Class<VM>) : androidx.fragment.app.Fragment() {

    lateinit var viewModel: VM
    open lateinit var binding: DB
    private lateinit var snackBar: Snackbar

    private fun init(inflater: LayoutInflater, container: ViewGroup) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
    }

    open fun init() {}
    @LayoutRes
    abstract fun getLayoutRes(): Int

    private fun getViewM(): VM = ViewModelProvider(this).get(mViewModelClass)


    open fun onInject() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewM()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        init(inflater, container!!)
        init()
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
    open fun refresh() {}

    fun GetSharedPre(): SharedPre {
        return SharedPre.getInstance(requireActivity())!!
    }

    fun getSharedPre():SharedPre{
        return SharedPre.getInstance(requireContext())!!
    }

    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    open fun startActivityWithAnimation(intent: Intent?, animationType: Int) {
        startActivity(intent)
        when (animationType) {
            Appconstants.SLIDE_IN_RIGHT -> requireActivity().overridePendingTransition(
                R.anim.slide_right,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_BOTTOM -> requireActivity().overridePendingTransition(
                R.anim.slide_down,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_LEFT -> requireActivity().overridePendingTransition(
                R.anim.slide_left,
                R.anim.slide_out_up
            )
            Appconstants.SLIDE_IN_TOP -> requireActivity().overridePendingTransition(
                R.anim.slide_up,
                R.anim.slide_out_up
            )
            Appconstants.SCALE -> requireActivity().overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
        }
    }
}