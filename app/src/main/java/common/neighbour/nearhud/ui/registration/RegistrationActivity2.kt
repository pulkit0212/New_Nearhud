package common.neighbour.nearhud.ui.registration

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.databinding.ActivityRegistration2Binding
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.ui.setting.adapter.OccupationAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity2 : BaseActivity(), HomeNavigator {

    private lateinit var binding: ActivityRegistration2Binding
    private val viewModel: HomeViewModel by viewModels()
    private val viewModelMain: MainViewModel by viewModels()
    private val loader by lazy { ProgressView.getLoader(this) }

    var date: Date? = null

    // initialize for image
    private val bitmap: Bitmap? = null
    var profileUri: Uri? = null
    var profilePicRef: StorageReference? = null
    var generatedImageFilePath = "abc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_registration2)
        viewModel.navigator=this
        AccessClickListener()
    }

    private fun AccessClickListener() {

        binding.etNeighbourName.text = Common.savedGrpInfo.name

        binding.btUploadImage.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(this@RegistrationActivity2, ProfileCameraActivity::class.java)
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

        binding.btnSubmit.setOnClickListener {
            when {
                binding.etFirstName.text.toString().isNullOrEmpty() -> {
                    showCustomAlert(getString(R.string.fname_empty),binding.root)
                    binding.etFirstName.requestFocus()
                }
                binding.etNeighbourName.text.toString().isNullOrEmpty() -> {
                    showCustomAlert(getString(R.string.lname_neighbour),binding.root)
                    binding.etNeighbourName.requestFocus()
                }
                else -> {
                    val userUid = sharedPre().userId!!
                    val storageRef = Firebase.storage.reference
                    val filename = System.currentTimeMillis()
                    profilePicRef = storageRef.child("/profile_images/$userUid/$filename")
                    uploadData(profileUri)
                }
            }
        }
        binding.etOccupation.setOnClickListener {
            val dialog = BottomSheetDialogs.getOccupationDialog(this)
            val rvOccupation = dialog.findViewById<RecyclerView>(R.id.rvOccupation)
            var occupationAdapter: OccupationAdapter? = null

            occupationAdapter = OccupationAdapter(this,object:OccupationAdapter.OnOccupationClick{
                override fun onOccupationClick(occupation: String) {
                    binding.etOccupation.text = occupation
                    dialog.dismiss()
                }

            })
            rvOccupation.adapter = occupationAdapter
            rvOccupation.isNestedScrollingEnabled = false
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvOccupation.layoutManager = layoutManager

            //API
            viewModelMain?.getOccupation()?.observe(this) {
                when (it.status) {
                    BaseDataSource.Resource.Status.SUCCESS -> {
                        loader.dismiss()
                        occupationAdapter.setData(it.data!!.data, viewModelMain)
                        occupationAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            dialog.show()
        }
    }

    private fun uploadData(dataUri: Uri?) {
        if (dataUri != null) {
            loader.show()
            profilePicRef!!.putFile(dataUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl
                        .addOnCompleteListener { task ->
                            generatedImageFilePath = task.result.toString()
                            callInsertUserAPI(generatedImageFilePath)
                        }
                }
                .addOnFailureListener { e ->
                    loader.dismiss()
                    showCustomAlert(e.message,binding.root)
                }

        } else {
            loader.show()
            generatedImageFilePath = "profile pic"
            callInsertUserAPI(generatedImageFilePath)
        }

    }

    private fun callInsertUserAPI(generatedImageFilePath: String) {
        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val strDate = dateFormat.format(date)
        viewModel.insertUserAPI(
            sharedPre().userId!!,
            binding.etFirstName.text.toString(),
            binding.etLastName.text.toString(),
            binding.etOccupation.text.toString(),
            generatedImageFilePath,
            strDate.toString(),
            sharedPre().userMobile!!,
            Common.userLatlng,
            Common.savedGrpInfo.groupId,
            Common.savedGrpInfo.city,
            Common.savedGrpInfo.state,
            Common.address,
            Common.zip,
            Common.email
        ).observe(this,{

            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    //loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    showCustomAlert("Signup Successfully",binding.root)
                    startActivityWithAnimation(
                        Intent(
                            this,
                            MainActivity::class.java
                        ), Appconstants.SLIDE_IN_BOTTOM
                    )
                    finishAffinity()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    showCustomAlert(it?.message,binding.root)
                }
            }

        })

    }



    override fun onLoading(isLoading: Boolean) {
        loading(isLoading)
    }

    override fun OnErrorMessage(message: String) {
        showCustomAlert(message,binding.root)
    }

    override fun Logout() {

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppConstance.RC_PROFILE) {
            if (data != null) {
                profileUri = Uri.fromFile(AppConstance.profileMediaFile)
                Glide.with(this).load(AppConstance.profileMediaFile)
                    .placeholder(R.drawable.placeholder_image)
                    .into(binding.ivProfilePic)
            }
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            //&& data!=null && data.getData() !=null
//            when (requestCode) {
//                CAPTURE_IMAGE_REQUEST -> {
//                    try {
//                        profileUri = data?.data
//                        val file: String =
//                            FileAccess.getPath(this, profileUri)
//                        finalFile = File(file)
//                        Glide.with(this).load(profileUri)
//                            .placeholder(R.drawable.placeholder_image)
//                            .into(binding.ivProfilePic)
//                        //  viewmodel.SaveImageOnServer(finalFile)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    // } uploadData(selectedImage);
//                }
//                PICK_IMAGE_REQUEST -> {
//                    // filePath=data.getData();
//                    profileUri = data?.data
//                    try {
//                        Glide.with(this).load(profileUri)
//                            .placeholder(R.drawable.placeholder_image)
//                            .into(binding.ivProfilePic)
//
//                        //uploadData(selectedImage);
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }
}