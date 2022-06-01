package common.neighbour.nearhud.ui.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.databinding.ActivityMainBinding
import common.neighbour.nearhud.ui.login.viewmodel.LoginNavigator
import common.neighbour.nearhud.ui.login.viewmodel.LoginViewmodel
import common.neighbour.nearhud.ui.registration.RegistrationActivity1
import common.neighbour.nearhud.ui.welcome.WelcomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() , Animation.AnimationListener,LoginNavigator {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewmodel by viewModels()
    private lateinit var context: Context
    private lateinit var animationListener:Animation.AnimationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)
        context=this
        viewModel.navigator = this
        animationListener=this
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        lifecycleScope.launch (Dispatchers.Main){
            delay(1000L)
            binding.logo.visibility= View.VISIBLE
            myAnim.setAnimationListener(animationListener)
            binding.logo.startAnimation(myAnim)
        }
        // workStation()

    }

    private fun workStation() {
        if (sharedPre().jwtToken.equals("")){
            viewModel.generateToken(
                "pulkitappdeveloper",
                "shizasoftwaredeveloper"
            )
                .observe(this, Observer {
                    if (it != null) {
                        sharedPre().setJwtToken(it.data.token)
                        viewModel.checkUser(sharedPre().userId!!)
                            .observe(this,
                                Observer
                                {
                                    showCustomAlert("Success", binding.root)
                                    if (it.data.userExist){
                                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else{
                                        val intent =
                                            Intent(this@MainActivity, RegistrationActivity1::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                    })
                                }
                })
        }
        else{
            viewModel.checkUser(sharedPre().userId!!)
                .observe(this,
                    Observer
                    {
                        if (it.data.userExist){
                            val intent =
                                Intent(this@MainActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val intent =
                                Intent(this@MainActivity, RegistrationActivity1::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
        }

    }

    private fun checkNetwork() {
        lifecycleScope.launchWhenResumed {
            networkAvailableFlow().collectLatest { networkIsAvailable ->
                println(networkIsAvailable)
                if(networkIsAvailable){
                    binding.mainTool.visibility = View.VISIBLE
                    binding.internerError.visibility = View.GONE
                    if (sharedPre().userId.equals("")) {
                        generateToken()
                    }
                    else{
                        workStation()
                    }
                }else{
                    binding.mainTool.visibility = View.GONE
                    binding.internerError.visibility = View.VISIBLE
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun networkAvailableFlow(): Flow<Boolean> = callbackFlow {
        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    offer(true)
                }

                override fun onLost(network: Network) {
                    offer(false)
                }

                override fun onUnavailable() {
                    offer(false)
                }
            }
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.registerNetworkCallback(NetworkRequest.Builder().run {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            build()
        }, callback)
        awaitClose {
            manager.unregisterNetworkCallback(callback)
        }
    }

    override fun onAnimationStart(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        lifecycleScope.launch (Dispatchers.Main){
            delay(2000L)
           // checkNetwork()
            if (sharedPre().userId.equals("")) {
                generateToken()
            }
            else{
                workStation()
            }
//            if(isNetworkConnected()){
//                binding.internerError.visibility = View.GONE
//                binding.mainTool.visibility = View.VISIBLE
//                if (sharedPre.userId.equals("")) {
//                    generateToken()
//                }
//                else{
//                    workStation()
//                }
//            }
//            else{
//                binding.mainTool.visibility = View.GONE
//                binding.internerError.visibility = View.VISIBLE
//
//            }
//            if(sharedPre.isLoggedIn){
//                startActivityWithAnimation(Intent(context, HomeActivity::class.java), Appconstants.SCALE)
//            }else{
//                startActivityWithAnimation(Intent(context, WelcomeActivity::class.java),Appconstants.SCALE)
//            }
            //finishAffinity()
        }
    }

    private fun generateToken() {
        viewModel.generateToken(
            "pulkitappdeveloper",
            "shizasoftwaredeveloper"
        )
            .observe(this, Observer {
                if (it != null) {
                    sharedPre().setJwtToken(it.data.token)
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onLoading(isLoading: Boolean) {

    }

    override fun OnErrorMessage(message: String) {

    }
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}