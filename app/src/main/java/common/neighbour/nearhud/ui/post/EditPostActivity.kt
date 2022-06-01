package common.neighbour.nearhud.ui.post

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.core.extensions.gone
import common.neighbour.nearhud.core.extensions.likeDoubleTap
import common.neighbour.nearhud.databinding.ActivityEditPostBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.camera.CameraActivity
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.ui.map.SearchLocationActivity
import common.neighbour.nearhud.ui.registration.ProfileCameraActivity
import common.neighbour.nearhud.ui.setting.MyPostActivity
import kohii.v1.exoplayer.Kohii
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditPostActivity : NewBaseActivity<MainViewModel, ActivityEditPostBinding>() {

    private val loader by lazy { ProgressView.getLoader(this) }
    var postLocation = ""
    var videoRef: StorageReference? = null
    var generatedVideoFilePath: String? = null
    var generatedImageFilePath:String? = null
    var updateDate: String? = null
    var date: Date? = null
    var postUri: Uri? = null
    var postType:String = ""

    override fun getViewBinding()= ActivityEditPostBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setupToolbar()
        initViews()
        setdata()
    }

//    private fun setupToolbar(){
//        with(binding.toolbar) {
//            //centerTitle()
//            binding.toolbar.setNavigationOnClickListener { finish() }
//        }
//    }

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.viewChangeImage.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(this@EditPostActivity, EditPostCameraActivity::class.java)
                            startActivityForResult(intent, AppConstance.RC_PROFILE)
                            //showImagePickerOptions()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
        }

        binding.rlUpdateLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, AppConstance.RC_PLACE)
        }
        binding.btnPost.setOnClickListener {
            if (Common.isPostMediaChange){
                val userUid = sharedPre().userId
                val filename = System.currentTimeMillis()
                val storageRef = Firebase.storage.reference
                videoRef = storageRef.child("/images_videos/$userUid/$filename")
                uploadData(postUri!!)
            }
            else{
                updatePostAPI()
            }

        }
    }

    private fun uploadData(uri: Uri) {
        //update date
        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        updateDate = dateFormat.format(date)

        loader.show()
        videoRef!!.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl
                    .addOnCompleteListener { task ->
                        if (AppConstance?.postType == "image") {
                            generatedImageFilePath = task.result.toString()
                            generatedVideoFilePath = ""
                            // showCustomAlert("image done",binding.root)
                            createPostAPI(
                                binding.etPostCaption.text.toString(),
                                sharedPre().userId,
                                Common.updatePostData.createdOn,
                                updateDate,
                                generatedVideoFilePath!!,
                                generatedImageFilePath!!
                            )
                        } else {
                            generatedImageFilePath = ""
                            generatedVideoFilePath = task.result.toString()
                            // showCustomAlert("video done",binding.root)
                            createPostAPI(binding.etPostCaption.text.toString(),
                                sharedPre().userId,
                                Common.updatePostData.createdOn,
                                updateDate,
                                generatedVideoFilePath!!,
                                generatedImageFilePath!!
                            )
                        }
                    }
            }
            .addOnFailureListener { e ->
                loader.dismiss()
                showCustomAlert(e.message,binding.root)
            }
    }

    private fun updatePostAPI() {
        //update date
        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        updateDate = dateFormat.format(date)

        if(Common.updatePostData.image == ""){
            createPostAPI(binding.etPostCaption.text.toString(),
                sharedPre().userId,
                Common.updatePostData.createdOn,
                updateDate,
                Common.updatePostData.video,
                ""
            )
        }else{
            createPostAPI(binding.etPostCaption.text.toString(),
                sharedPre().userId,
                Common.updatePostData.createdOn,
                updateDate,
                "",
                Common.updatePostData.image)
        }
    }

    private fun setdata() {

        if(Common.updatePostData.image == ""){
            //binding.ivPhoto.gone()
            Glide.with(this).load(Common.updatePostData.video).into(binding.ivPhoto)
        }else{
           // binding.videoView.gone()
            Glide.with(this).load(Common.updatePostData.image).into(binding.ivPhoto)
        }

        if(Common.updatePostData.location.isNotEmpty()){
            binding.tvLocation.text = Common.updatePostData.location
        }else{
            binding.tvLocation.text = "Location"
        }

        binding.etPostCaption.setText(Common.updatePostData.bodyText)
    }

    private fun createPostAPI(caption: String,
                              userId: String?,
                              curDate: String?,
                              updateDate: String?,
                              generatedVideoFilePath: String,
                              generatedImageFilePath: String,)
    {
        viewModel.updatePost(
            caption,
            userId,
            curDate,
            updateDate,
            generatedVideoFilePath,
            generatedImageFilePath,
            sharedPre().userCity,
            sharedPre().userGroupId!!,
            sharedPre().userState!!,
            binding.tvLocation.text.toString(),
            Common.updatePostData.postId.toString()
        )
            .observe(this, androidx.lifecycle.Observer {
                when (it.status) {
                    BaseDataSource.Resource.Status.LOADING -> {
                        loader.show()
                    }
                    BaseDataSource.Resource.Status.SUCCESS -> {
                        loader.dismiss()
                        showCustomAlert(it.data!!.message,binding.root)
                        finish()
                    }
                    BaseDataSource.Resource.Status.ERROR -> {
                        loader.dismiss()
                        Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
                        //showCustomAlert(it.data!!.message)
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppConstance.RC_PLACE) {
            if (data != null) {
                val tempId = data.getStringExtra(AppConstance.SELECTED_PLACE_ID)
                val tempName = data.getStringExtra(AppConstance.SELECTED_PLACE_NAME)
                binding.tvLocation.text = tempName
                postLocation = tempName!!
                //lat lng by address
                var addressList: List<Address>? = null
                if (tempName != null || tempName != "") {
                    val geocoder = Geocoder(this)
                    try {
                        addressList = geocoder.getFromLocationName(tempName, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (addressList!!.isEmpty()) {
                        Toast.makeText(this, "Address not found.", Toast.LENGTH_SHORT).show()
                    } else {
                        val address = addressList[0]
                        //searchLocation = LatLng(address.latitude, address.longitude)
                    }
                } else {
                    Toast.makeText(this, "Please Enter Location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (resultCode == AppConstance.RC_UPDATE_POST) {
            if (data != null) {
                Common.isPostMediaChange = true
                postUri = Uri.fromFile(AppConstance.mediaFile)
                postType = AppConstance.postType
                Glide.with(this)
                    .load(postUri)
                    .centerCrop()
                    .into(binding.ivPhoto)
//                if (postType == "image"){
//                    setupForImage(AppConstance.mediaFile)
//                }
//                else{
//                    setupForVideo(AppConstance.mediaFile)
//                }
            }
        }
    }
//    private fun setupForImage(mediaFile: File?) {
//        binding.videoView.gone()
//        Glide.with(this)
//            .load(mediaFile)
//            .centerCrop()
//            .into(binding.ivPhoto)
//    }
//
//    private fun setupForVideo(mediaFile: File?) {
//        binding.ivPhoto.gone()
//        val dataSpec = DataSpec(Uri.fromFile(mediaFile))
//        val fileDataSource = FileDataSource()
//        fileDataSource.open(dataSpec)
//
//        val kohii = Kohii[this]
//        kohii.register(this).addBucket(binding.root)
//        kohii.setUp(Uri.fromFile(mediaFile)).bind(binding.videoView)
//
//    }
}