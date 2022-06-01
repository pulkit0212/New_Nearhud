package common.neighbour.nearhud.ui.post

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.LatLng
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.core.extensions.*
import common.neighbour.nearhud.databinding.ActivityEditPostBinding
import common.neighbour.nearhud.databinding.ActivityEditPostCameraBinding
import common.neighbour.nearhud.databinding.MainActivityBinding
import common.neighbour.nearhud.newUi.LocationProvider
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.camera.CaptureView
import common.neighbour.nearhud.ui.camera.ClickedPhotoFragment
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class EditPostCameraActivity: NewBaseActivity<MainViewModel, ActivityEditPostCameraBinding>() {
    override fun getViewBinding() = ActivityEditPostCameraBinding.inflate(layoutInflater)
    var pictureIsTaken = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        WorkStation()
    }

    var imagePickerCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data != null){
            val extension = getExtension(it.data!!.data!!)
            if(extension == "mp4"){
                AppConstance?.postType = "video"
            }else{
                AppConstance?.postType = "image"
            }
            viewModel?.postIsFromCamera = false
            viewModel?.address = ""
            AppConstance?.mediaFile = convertToFile(it.data!!.data!!,"file",if(AppConstance?.postType == "image") ".jpeg" else ".mp4")
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
            setLifecycleOwner(this@EditPostCameraActivity)
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
                binding.cameraView.close()
                AppConstance.mediaFile = result.file
                AppConstance.postType = "video"
                goToPreviewFragment()
            }

            override fun onPictureTaken(result: PictureResult) {
                //showLoader(binding.contianer)
                binding.cameraView.close()
                pictureIsTaken = true
                result.toFile(AppConstance.mediaFile!!) {
                    AppConstance.mediaFile = it
                    AppConstance.postType = "image"
                }
                goToPreviewFragment()
//                if(viewModel!!.lastLocation != null){
//                    goToPreviewFragment()
//                }
            }
        })

        binding.cutBtn.setOnClickListener {
            finish()
            overridePendingTransition(  R.anim.slide_down,
                R.anim.slide_out_down)
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
                viewModel?.postIsFromCamera = true
                AppConstance?.mediaFile = File(
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "Nearhud - ${System.currentTimeMillis()}.jpg"
                )
                binding.cameraView.takePicture()
            }

            override fun onCaptureLongClick(view: CaptureView) {
                viewModel?.postIsFromCamera = true
                viewModel?.latLongList = LinkedList<LatLng>()
                binding.timeCard.visibility = View.VISIBLE
                binding.time.visibility = View.VISIBLE
                binding.cameraView.mode = Mode.VIDEO
                AppConstance?.mediaFile = File(
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "Nearhud-${System.currentTimeMillis()}.mp4"
                )
                binding.cameraView.takeVideoSnapshot(AppConstance?.mediaFile!!)
            }

            override fun onSecondElapsed(seconds: Int) {
                binding.time.text = seconds.secondsToTimeFormat()
            }

            override fun onVideoEnds(view: CaptureView) {
                binding.cameraView.stopVideo()
                viewModel?.encodedPoly = viewModel?.latLongList?.toStaticMapApiFormat()
            }

            override fun onGalleryCLick(view: CaptureView) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //intent.type = "image/* video/*"
                imagePickerCallback.launch(intent)
            }

            override fun onCameraToggleClick(view: CaptureView) {
                binding.cameraView.toggleFacing()
            }
        }
    }


    fun goToPreviewFragment(){
        val i = Intent()
        setResult(AppConstance.RC_UPDATE_POST,i)
        finish()
    }

}