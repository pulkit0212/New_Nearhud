package common.neighbour.nearhud.ui.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.MainActivityBinding
import common.neighbour.nearhud.ui.camera.CameraActivity

class MainActivity : NewBaseActivity<MainViewModel, MainActivityBinding>() ,
    NavigationView.OnNavigationItemSelectedListener {

    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)

    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentById(R.id.container) is MainFragment){
            finish()
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.activity = this
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
        binding.container.post {
            viewModel.switchHomePage(this)
        }

        initializeViews()

    }

    private fun initializeViews() {
        binding.fab.setOnClickListener {
            startActivityWithAnimation(
                Intent(this,
                    CameraActivity::class.java
                ), Appconstants.SLIDE_IN_TOP
            )
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                    viewModel.switchHomePage(this)
            }
            R.id.navigation_profile -> {
                viewModel.switchSettingPage(this)
            }
            else -> {
                Toast.makeText(this,"Menu Not Found",Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

}