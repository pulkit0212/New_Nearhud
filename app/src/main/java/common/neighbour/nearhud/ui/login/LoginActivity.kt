package common.neighbour.nearhud.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.databinding.ActivityLoginBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.ui.login.viewmodel.LoginNavigator
import common.neighbour.nearhud.ui.login.viewmodel.LoginViewmodel

class LoginActivity : BaseActivity(), LoginNavigator {

    private val viewModel: LoginViewmodel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var context: Context
    private var handler= Handler()
    private var runnable:Runnable?=null
    private val loader by lazy { ProgressView.getLoader(this) }

    // private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var loginActivityView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        //setContentView(R.layout.activity_login)
        viewModel.navigator = this
        context = this
       // workStation()

        binding.btnVerify.setOnClickListener {
            if (binding.etNumber.text.toString() == "") {
                //binding.btnVerify.setOnClickListener(null)
                showCustomAlert(getString(R.string.error_on_mobile_number), binding.root)
            }
            else{
                val firstNumber = binding.etNumber.text[0].toString()
                Log.d("NUMBEROO", binding.etNumber.text[1].toString())
                if (binding.etNumber.text.toString().length < 10) {
                    //binding.btnVerify.setOnClickListener(null)
                    showCustomAlert(getString(R.string.error_on_mobile_number2), binding.root)
                }
                else if (firstNumber == "0" || firstNumber == "1" || firstNumber == "2" || firstNumber == "3" || firstNumber == "4" || firstNumber == "5") {
                    showCustomAlert(getString(R.string.error_on_mobile_number2), binding.root)
                }
                else{
                    sharedPre().setUserMobile(binding.etNumber.text.toString())
                    viewModel.sendOtp(binding.etNumber.text.toString()).observe(this) {
                        when (it.status) {
                            BaseDataSource.Resource.Status.LOADING -> {
                                loader.show()
                            }
                            BaseDataSource.Resource.Status.SUCCESS -> {
                                loader.dismiss()
                                if (it.data!!.code == 500){
                                    viewModel.generateToken("pulkitappdeveloper", "shizasoftwaredeveloper")
                                        .observe(this, Observer {
                                            if (it != null) {
                                                sharedPre().setJwtToken(it.data.token)
                                                sendOTP()
                                            }
                                        })
                                }
                                else{
                                    sharedPre().setEnterOtp(it.data!!.data.code.toString())
                                    startActivity(Intent(context, OtpActivity::class.java)
                                        .putExtra("OTP",it.data.data.code.toString()))
                                    finish()
                                }
                            }
                            BaseDataSource.Resource.Status.ERROR -> {
                               loader.dismiss()
                                Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                                viewModel.generateToken("pulkitappdeveloper", "shizasoftwaredeveloper")
                                    .observe(this, Observer {
                                        if (it != null) {
                                            sharedPre().setJwtToken(it.data.token)
                                            sendOTP()
                                        }
                                    })
                            }
                        }
                    }
                }
            }

        }
    }

    private fun sendOTP() {
        viewModel.sendOtp(binding.etNumber.text.toString()).observe(this) {
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    sharedPre().setEnterOtp(it.data!!.data.code.toString())
                    startActivity(Intent(context, OtpActivity::class.java)
                        .putExtra("OTP",it.data.data.code.toString()))
                    finish()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        }
    }

    override fun onLoading(isLoading: Boolean) {
        loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,loginActivityView)

    }
}