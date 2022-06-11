package common.neighbour.nearhud.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.databinding.ActivityOtpBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.repositories.methods.FirbaseAuthActions
import common.neighbour.nearhud.ui.contact_share.ViewReferActivity
import common.neighbour.nearhud.ui.login.viewmodel.LoginNavigator
import common.neighbour.nearhud.ui.login.viewmodel.LoginViewmodel
import common.neighbour.nearhud.ui.registration.RegistrationActivity1

class OtpActivity : BaseActivity(), FirbaseAuthActions , LoginNavigator {

    private lateinit var binding: ActivityOtpBinding
    var mobile: String? = null

    //var action:FirbaseAuthActions?=null
    private val viewModel: LoginViewmodel by viewModels()
    // private var mAuth: FirebaseAuth? = null

    private var timer: CountDownTimer? = null
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_otp)
        // action=this
        viewModel.navigator = this

        showCustomAlert("Otp for verification is ${intent.getStringExtra("OTP").toString()}",binding.root)

        //  mAuth = FirebaseAuth.getInstance()
        //  viewModel.MobileAuthCheck(this, "+91"+sharedPre.userMobile.toString(), action!!)
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val v = String.format("%02d", millisUntilFinished / 60000)
                val va = (millisUntilFinished % 60000 / 1000).toInt()
                binding.tvAuthOtpCounter.text = (v + ":"+ String.format("%02d", va))
                // binding.resendButton.visibility = View.INVISIBLE
            }

            override fun onFinish() {
                binding.tvResendtag.visibility = View.VISIBLE
                binding.tvAuthOtpResend.visibility = View.VISIBLE
                binding.tvAuthOtpCounter.visibility = View.GONE
                binding.tvWaiting.visibility = View.GONE
            }
        }
        //  viewModel.MobileAuthCheck(this, "+91"+sharedPre.userMobile.toString(), action!!)
        timer!!.start()

        binding.btnVerify.setOnClickListener {
            val code: String = binding.editTextCode.text.toString().trim()
            if(code.equals("")){
                showCustomAlert("Please Enter OTP",binding.root)
            }
            else if(code.length<5){
                showCustomAlert("Incorrect OTP",binding.root)
            }
            else if(sharedPre().enterOtp.equals(binding.editTextCode.text.toString())){
                startOtherActivity()
            }
            else{
                showCustomAlert("Incorrect OTP",binding.root)
            }
//            if (code.isEmpty() || code.length < 5) {
//                binding.editTextCode.error = "Enter valid code"
//                binding.editTextCode.requestFocus()
//                return@setOnClickListener
//            }else{
//
//                if(sharedPre.enterOtp.equals(binding.editTextCode.text.toString())){
//                    startOtherActivity()
//                }
//                else{
//                    showCustomAlert("please enter valid otp",binding.root)
//                }
//
//                //viewModel.verifyVerificationCode(binding.editTextCode.text.toString(), this, action!!)
//            }
        }

        binding.tvAuthOtpResend.setOnClickListener {
            binding.editTextCode.setText("")
            viewModel.sendOtp(sharedPre().userMobile.toString())
                .observe(this, Observer {
                    when (it.status) {
                        BaseDataSource.Resource.Status.LOADING -> {
                            loader.show()
                        }
                        BaseDataSource.Resource.Status.SUCCESS -> {
                            loader.dismiss()
                            showCustomAlert("Otp for verification is ${it.data!!.data.code}",binding.root)
                            viewModel.sharedPre.setEnterOtp(it.data.data.code.toString())
                            timer!!.start()
                            binding.tvAuthOtpResend.visibility = View.GONE
                            binding.tvResendtag.visibility = View.GONE
                            binding.tvAuthOtpCounter.visibility = View.VISIBLE
                            binding.tvWaiting.visibility = View.VISIBLE
                        }
                        BaseDataSource.Resource.Status.ERROR -> {
                            loader.dismiss()
                            Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                            //showCustomAlert(it.data!!.message)
                        }
                    }
                })
        }
    }

    private fun startOtherActivity() {
        timer!!.cancel()
        sharedPre().ScreenType= AppConstance.SCREEN_HOME
        if (sharedPre().jwtToken.equals("")){
            viewModel.generateToken(
                "pulkitappdeveloper",
                "shizasoftwaredeveloper"
            )
                .observe(this, Observer { it ->
                    if (it != null) {
                        sharedPre().setJwtToken(it.data.token)
                        sharedPre().setUserId(sharedPre().userMobile!!)
                        viewModel.checkUser(sharedPre().userId!!)
                            .observe(this,
                                Observer
                                {
                                    showCustomAlert(it.message, binding.root)
                                    if (it != null ) {
//                                        showCustomAlert("Success", binding.root)
//                                        showCustomAlert(it.data.toString(), binding.root)
                                        if (it.data.userExist){
                                            // sharedPre.setUserId(sharedPre.userMobile!!)
                                            val intent =
                                                Intent(this@OtpActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else{
                                            //sharedPre.setUserId(sharedPre.userMobile!!)
                                            if(it.data.referedData.isEmpty()){
                                                val intent =
                                                    Intent(this@OtpActivity, RegistrationActivity1::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            else{
                                                Common.referedData = it.data.referedData
                                                val intent = Intent(this@OtpActivity, ViewReferActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }

                                        }
                                    }
                                })
                    }
                })
        }
        else{
            sharedPre().setUserId(sharedPre().userMobile!!)
            viewModel.checkUser(sharedPre().userId!!)
                .observe(this,
                    Observer
                    {
                        //showCustomAlert("Success", binding.root)
                        showCustomAlert(it.message.toString(), binding.root)
                        if (it != null ) {
                            if (it.data.userExist){
                                // sharedPre.setUserId(sharedPre.userMobile!!)
                                val intent =
                                    Intent(this@OtpActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                //sharedPre.setUserId(sharedPre.userMobile!!)
                                if(it.data.referedData.isEmpty()){
                                    val intent =
                                        Intent(this@OtpActivity, RegistrationActivity1::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Common.referedData = it.data.referedData
                                    val intent =
                                        Intent(this@OtpActivity, ViewReferActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        }
                    })
        }
    }

    override fun VerificationCodeSent() {
        timer!!.start()
    }

    override fun startActivity(uid: String, phoneNumber: String?) {

        //  startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun Error(message: String) {
        timer!!.cancel()
        showCustomAlert(message,binding.root)
    }

    override fun TimeOut() {
        binding.tvResendtag.visibility = View.VISIBLE
        binding.tvAuthOtpResend.visibility = View.VISIBLE
        binding.tvAuthOtpCounter.visibility = View.GONE
        binding.tvWaiting.visibility = View.GONE
        timer!!.cancel()
    }

    override fun onLoading(isLoading: Boolean) {
        loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,binding.root)
    }


}