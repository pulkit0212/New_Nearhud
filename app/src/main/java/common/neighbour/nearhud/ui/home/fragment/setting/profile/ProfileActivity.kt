package common.neighbour.nearhud.ui.home.fragment.setting.profile

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
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
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.databinding.ActivityMyPostBinding
import common.neighbour.nearhud.databinding.ProfileFragmentBinding
import common.neighbour.nearhud.dialogs.BottomSheetDialogs
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.userprofile.UserProfileResponse
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.ui.login.LoginActivity
import common.neighbour.nearhud.ui.post.EditPostActivity
import common.neighbour.nearhud.ui.registration.ProfileCameraActivity
import common.neighbour.nearhud.ui.setting.adapter.NeighbourAdapter
import common.neighbour.nearhud.ui.setting.adapter.OccupationAdapter
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : NewBaseActivity<MainViewModel, ProfileFragmentBinding>(){
    override fun getViewBinding() = ProfileFragmentBinding.inflate(layoutInflater)

    private val loader by lazy { ProgressView.getLoader(this) }
    var date: Date? = null
    var profileUri: Uri? = null
    var profilePicRef: StorageReference? = null
    var generatedImageFilePath = "abc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.activity = this
        setUserData()
        initViews()
    }

    private fun initViews() {

        binding.viewChangeImage.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(this@ProfileActivity, ProfileCameraActivity::class.java)
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

        binding.btnUpdate.setOnClickListener {
            when {
                binding.etFirstName.text.toString().isNullOrEmpty() -> {
                    showCustomAlert(getString(R.string.fname_empty),binding.root)
                    binding.etFirstName.requestFocus()
                }
                binding.etEmail.text.toString().isNullOrEmpty() -> {
                    showCustomAlert(getString(R.string.email_empty),binding.root)
                    binding.etEmail.requestFocus()
                }
                else -> {
                     updateUserInfo(sharedPre().emailProfile!!)
                }
            }
        }

        binding.etOccupation.setOnClickListener {
            val dialog = BottomSheetDialogs.getOccupationDialog(this)
            val rvOccupation = dialog.findViewById<RecyclerView>(R.id.rvOccupation)
            var occupationAdapter: OccupationAdapter? = null

            occupationAdapter = OccupationAdapter(this,object :OccupationAdapter.OnOccupationClick{
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
            viewModel?.getOccupation()?.observe(this) {
                when (it.status) {
                    BaseDataSource.Resource.Status.SUCCESS -> {
                        occupationAdapter.setData(it.data!!.data, viewModel)
                        occupationAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            dialog.show()
        }
    }

    private fun updateUserInfo(userProfile: String) {
        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val strDate = dateFormat.format(date)
        viewModel.updateUserAPI(
            sharedPre().userId!!,
            binding.etFirstName.text.toString(),
            binding.etLastName.text.toString(),
            binding.etOccupation.text.toString(),
            userProfile,
            sharedPre().userCreateDate!!,
            strDate,
            sharedPre().userMobile!!,
            sharedPre().userLatLng!!,
            sharedPre().userGroupId!!,
            sharedPre().userCity!!,
            sharedPre().userState!!,
            sharedPre().userAddress!!,
            sharedPre().userZip!!,
            binding.etEmail.text.toString()
        ).observe(this,{
            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    sharedPre().setUserFName(binding.etFirstName.text.toString())
                    sharedPre().setUserLName(binding.etLastName.text.toString())
                    sharedPre().setUserOccupation(binding.etOccupation.text.toString())
                    sharedPre().setUserEmail(binding.etEmail.text.toString())
                    loader.dismiss()
                    showCustomAlert("User Update Successfully",binding.root)
                    finish()
                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    showCustomAlert(it?.message,binding.root)
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppConstance.RC_PROFILE) {
            if (data != null) {
                profileUri = Uri.fromFile(AppConstance.profileMediaFile)
                Glide.with(this).load(AppConstance.profileMediaFile)
                    .placeholder(R.drawable.placeholder_image)
                    .into(binding.profilePic)

                val userUid = sharedPre().userId!!
                val storageRef = Firebase.storage.reference
                val filename = System.currentTimeMillis()
                profilePicRef = storageRef.child("/profile_images/$userUid/$filename")
                uploadData(profileUri)
            }
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
                            sharedPre().setEmailProfile(generatedImageFilePath)
                            updateUserInfo(generatedImageFilePath)
                        }
                }
                .addOnFailureListener { e ->
                    loader.dismiss()
                    showCustomAlert(e.message,binding.root)
                }

        } else {
            loader.show()
            generatedImageFilePath = "profile pic"
            updateUserInfo(generatedImageFilePath)
        }

    }

    private fun setUserData() {
        if (sharedPre().emailProfile!!.isNotEmpty()){
            Glide.with(this).load(sharedPre().emailProfile).placeholder(R.drawable.user).into(binding.profilePic)
        }
        binding.etFirstName.setText(sharedPre().userFName)
        binding.etLastName.setText(sharedPre().userLName)
        binding.etEmail.setText(sharedPre().userEmail)
        binding.etOccupation.setText(sharedPre().userOccupation)
        binding.etNumber.text = sharedPre().userNumber
    }
}