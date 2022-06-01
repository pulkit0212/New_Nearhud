package common.neighbour.nearhud.ui.camera

import android.Manifest
import android.content.Intent
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
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.core.extensions.*
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentCameraBinding
import common.neighbour.nearhud.newUi.*
import common.neighbour.nearhud.repositories.constance.AppConstance
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class CameraFragment : NewBaseFragment<MainViewModel, FragmentCameraBinding>(MainViewModel::class.java),
    LocationProvider.EasyLocationCallback {
    override fun getLayoutRes() = R.layout.fragment_camera
    var pictureIsTaken = false

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        WorkStation()
    }

    private val locationPermissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            if (!locationPermissionAllowed()) {
                //showToast(R.string.please_allow_location_permission)
                //navController.popBackStack()
            }else{
                getAddress()
            }
        }

    lateinit var locationProvider: LocationProvider

    private fun enableLocationProvider() {
        locationProvider = LocationProvider.Builder(requireContext())
            .setInterval(500)
            .setFastestInterval(400)
            .setListener(this)
            .build()
        lifecycle.addObserver(locationProvider)
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
        if(!locationPermissionAllowed()) {
            locationPermissionCallback.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }else{
            if(AppConstance.mediaFile == null){
                getAddress()
            }
        }
        with(binding.cameraView) {
            setLifecycleOwner(viewLifecycleOwner)
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
            requireActivity().finish()
            requireActivity().overridePendingTransition(  R.anim.slide_down,
                R.anim.slide_out_down)
        }
    }

//    @ExperimentalCoroutinesApi
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentCameraBinding.bind(view)
//
//    }

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
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "Nearhud - ${System.currentTimeMillis()}.jpg"
                )
                binding.cameraView.takePicture()
            }

            override fun onCaptureLongClick(view: CaptureView) {
                enableLocationProvider()
                viewModel?.postIsFromCamera = true
                viewModel?.latLongList = LinkedList<LatLng>()
                binding.timeCard.visibility = View.VISIBLE
                binding.time.visibility = View.VISIBLE
                binding.cameraView.mode = Mode.VIDEO
                AppConstance?.mediaFile = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "Nearhud-${System.currentTimeMillis()}.mp4"
                )
                binding.cameraView.takeVideoSnapshot(AppConstance?.mediaFile!!)
            }

            override fun onSecondElapsed(seconds: Int) {
                binding.time.text = seconds.secondsToTimeFormat()
            }

            override fun onVideoEnds(view: CaptureView) {
                binding.cameraView.stopVideo()
                //locationProvider.removeUpdates()
                lifecycle.removeObserver(locationProvider)
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

    override fun onGoogleAPIClient(googleApiClient: GoogleApiClient?, message: String?) {

    }

    override fun onLocationUpdated(latitude: Double, longitude: Double) {
        viewModel?.latLongList?.add(LatLng(latitude,longitude))
    }

    override fun onLocationUpdateRemoved() {

    }

    private fun getAddress() {
        lifecycleScope.launch(Dispatchers.IO) {
            val locationIsOn = showGpsDialog()
            if (locationIsOn) {
//                viewModel?.lastLocation = awaitLastLocation()
                if (viewModel?.lastLocation != null) {
                    viewModel?.address = getAddressForLocation(viewModel?.lastLocation!!)
                } else {
                   //showCoroutineToast(R.string.could_not_get_location)
                }
//                if(navController.currentDestination!!.id == R.id.cameraFragment) {
//                    if (pictureIsTaken) {
//                        withContext(Dispatchers.Main.immediate) {
//                            goToPreviewFragment()
//                        }
//                    }
//                }else{
//                    cancel()
//                }
            }
            else {
               // navController.popBackStack()
               // showCoroutineToast(R.string.please_turn_on_gps)
            }
        }
    }

    fun goToPreviewFragment(){
        if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is ClickedPhotoFragment){
            requireActivity().replaceFragment(ClickedPhotoFragment(), R.id.container, "frag_click_photo")
        }
    }
}

