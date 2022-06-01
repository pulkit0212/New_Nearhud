package common.neighbour.nearhud.ui.camera

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import common.neighbour.nearhud.core.extensions.replaceFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.databinding.ActivityCameraBinding
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel

class CameraActivity : BaseActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        replaceFragment(CameraFragment(), R.id.container, "frag_home")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}