package common.neighbour.nearhud.ui.registration

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.core.extensions.convertToFile
import common.neighbour.nearhud.core.extensions.getAsDrawable
import common.neighbour.nearhud.core.extensions.secondsToTimeFormat
import common.neighbour.nearhud.databinding.ActivityProfileCameraBinding
import common.neighbour.nearhud.newUi.*
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.camera.CaptureView
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import java.util.*

class ProfileCameraActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileCameraBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_camera)

        WorkStation()
    }

    var imagePickerCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data != null){
            AppConstance?.profileMediaFile = convertToFile(it.data!!.data!!,"file",if(AppConstance?.postType == "image") ".jpeg" else ".mp4")
            goToPreviewFragment()
        }
    }

    fun WorkStation() {
        setupCaptureView()
        if (viewModel.flashOn) {
            binding.flashButton.setImageDrawable(getAsDrawable(R.drawable.ic_on_flash))
        } else {
            binding.flashButton.setImageDrawable(getAsDrawable(R.drawable.ic_off_flash))
        }

        with(binding.cameraView) {
            setLifecycleOwner(this@ProfileCameraActivity)
            facing = Facing.BACK
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.EXPOSURE_CORRECTION)
            mapGesture(Gesture.SCROLL_VERTICAL, GestureAction.FILTER_CONTROL_2)
        }
        binding.flashButton.setOnClickListener {
            setFlash()
        }

        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: VideoResult) {

            }

            override fun onPictureTaken(result: PictureResult) {
                binding.cameraView.close()
                result.toFile(AppConstance.profileMediaFile!!) {
                    AppConstance.profileMediaFile = it
                }
                goToPreviewFragment()
            }
        })

        binding.cutBtn.setOnClickListener {
        }
    }


    fun setFlash(){
        if (viewModel?.flashOn!!) {
            binding.cameraView.flash = Flash.OFF
            binding.flashButton.setImageDrawable(getAsDrawable(R.drawable.ic_off_flash))
            viewModel?.flashOn = false
        } else {
            binding.cameraView.flash = Flash.ON
            binding.flashButton.setImageDrawable(getAsDrawable(R.drawable.ic_on_flash))
            viewModel?.flashOn = true
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupCaptureView() {
        binding.captureView.eventCallbacks = object : CaptureView.EventCallbacks {
            override fun onCaptureClick(view: CaptureView) {
                AppConstance?.profileMediaFile = File(
                    this@ProfileCameraActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "Nearhud - ${System.currentTimeMillis()}.jpg"
                )
                binding.cameraView.takePicture()
            }

            override fun onCaptureLongClick(view: CaptureView) {

            }

            override fun onSecondElapsed(seconds: Int) {
                binding.time.text = seconds.secondsToTimeFormat()
            }

            override fun onVideoEnds(view: CaptureView) {

            }

            override fun onGalleryCLick(view: CaptureView) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePickerCallback.launch(intent)
            }

            override fun onCameraToggleClick(view: CaptureView) {
                binding.cameraView.toggleFacing()
            }
        }
    }

    fun goToPreviewFragment(){
        val i = Intent()
        setResult(AppConstance.RC_PROFILE,i)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}