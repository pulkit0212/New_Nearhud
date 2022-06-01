package common.neighbour.nearhud.ui.home.fragment.create_post

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.databinding.ActivityCreatePostBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.home.viewmodel.HomeNavigator
import common.neighbour.nearhud.ui.home.viewmodel.HomeViewModel
import common.neighbour.nearhud.ui.map.SearchLocationActivity
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.text.Editable

import android.text.TextWatcher
import common.neighbour.nearhud.common.Common

class CreatePostActivity  : BaseActivity() ,HomeNavigator{
    private lateinit var binding: ActivityCreatePostBinding
    private val viewModel: HomeViewModel by viewModels()
    var date: Date? = null
    var curDate: String? = null
    var postLocation = ""
    private val loader by lazy { ProgressView.getLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_create_post)
        viewModel.navigator=this
        AccessClickLIstener()

        if (Common.isPostUpdate){
            setData()
        }
    }

    private fun setData() {
        binding.editText.setText(Common.updatePostData.bodyText)
        if(Common.updatePostData.location.isNotEmpty()){
            binding.tvLocation.text = Common.updatePostData.location
        }else{
            binding.tvLocation.text = "Location"
        }
        binding.tagTitle.text = "Update Post"
        binding.btnPost.text = "Update"
    }

    private fun AccessClickLIstener() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //binding.btnPost.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()){
                    binding.btnPost.visibility = View.GONE
                }
                else{
                    binding.btnPost.visibility = View.VISIBLE
                }
            }
        })

        binding.rlUpdateLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, AppConstance.RC_PLACE)
        }

        binding.ivProfileName.text = sharedPre().userFName +" "+sharedPre().userLName
        if(!sharedPre().emailProfile.isNullOrEmpty()){
            Glide.with(this).load(sharedPre().emailProfile).placeholder(R.drawable.user).into(binding.ivProfileImg)
            // sharedPre.setEmailProfile(it.data[0].profilePicture)
        }else{
            Glide.with(this).load(R.drawable.user).placeholder(R.drawable.user).into(binding.ivProfileImg)
        }

        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        curDate = dateFormat.format(date)


        binding.btnPost.setOnClickListener {
            if (Common.isPostUpdate){
                updatePostAPI(binding.editText.text.toString(),
                    sharedPre().userId,
                    curDate,
                    "",
                    "",
                    Common.updatePostData.createdOn,
                    Common.updatePostData.postId.toString()
                )
            }
            else{
                createPostAPI(binding.editText.text.toString(),
                    sharedPre().userId,
                    curDate,
                    "",
                    ""
                )
            }

        }
    }

    private fun createPostAPI(
        textMess: String,
        uid: String?,
        curDate: String?,
        generatedVideoFilePath: String,
        generatedImageFilePath: String
    ) {
        viewModel.createPost(textMess,uid, curDate,generatedVideoFilePath, generatedImageFilePath,
            postLocation,
            sharedPre().userGroupId!!, sharedPre().userCity!!, sharedPre().userState!!
        )
            .observe(this, androidx.lifecycle.Observer {
                if(it.message.equals("success")){
                    showCustomAlert("Success",binding.root)
                    startActivityWithAnimation(
                        Intent(
                            this,
                            MainActivity::class.java
                        ), Appconstants.SLIDE_IN_BOTTOM
                    )
                    finishAffinity()
                }
                else{
                    loader.dismiss()
                    showCustomAlert(it?.message,binding.root)
                }
            })
    }

    private fun updatePostAPI(
        textMess: String,
        uid: String?,
        curDate: String?,
        generatedVideoFilePath: String,
        generatedImageFilePath: String,
        createdDate: String,
        postId: String
    ) {
        viewModel.updatePost(textMess,uid, curDate,generatedVideoFilePath, generatedImageFilePath,
            postLocation,createdDate,postId,
            sharedPre().userGroupId!!, sharedPre().userCity!!, sharedPre().userState!!
        )
            .observe(this, androidx.lifecycle.Observer {
                if(it.message.equals("success")){
                    showCustomAlert("Success",binding.root)
                    finish()
                }
                else{
                    loader.dismiss()
                    showCustomAlert(it?.message,binding.root)
                }
            })
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onLoading(isLoading: Boolean) {

    }

    override fun OnErrorMessage(message: String) {
    }

    override fun Logout() {
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
    }
}