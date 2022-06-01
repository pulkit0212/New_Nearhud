package common.neighbour.nearhud.ui.camera

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.setting.Appconstants
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.core.extensions.getAsDrawable
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentCreateMediaPostBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.ui.map.SearchLocationActivity
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateMediaPostFragment : NewBaseFragment<MainViewModel, FragmentCreateMediaPostBinding>(
    MainViewModel::class.java){

    override fun getLayoutRes() = R.layout.fragment_create_media_post
    var videoRef: StorageReference? = null
    var generatedVideoFilePath: String? = null
    var generatedImageFilePath:String? = null
    var date: Date? = null
    var curDate: String? = null
    var postLocation = ""
    private val loader by lazy { ProgressView.getLoader(requireActivity()) }

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        WorkStation()
    }

    fun WorkStation() {
        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
        //binding.lifecycleOwner = this
        //requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //cur date
        date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        curDate = dateFormat.format(date)

        if (viewModel.postIsFromCamera) {
            binding.rlUpdateLocation.visibility = View.GONE
        }
        if (viewModel.placeResult != null) {
            binding.tvLocation.text = viewModel!!.placeResult!!.name
            binding.ivarrw.setImageDrawable(getAsDrawable(R.drawable.ic_cross_cut))
            binding.ivarrw.setOnClickListener {
                binding.tvLocation.text = "Location"
                binding.ivarrw.setImageDrawable(getAsDrawable(R.drawable.ic_black_right_angle))
            }
        }

        with(binding) {
            rlUpdateLocation.setOnClickListener {
                val intent = Intent(requireContext(), SearchLocationActivity::class.java)
                startActivityForResult(intent, AppConstance.RC_PLACE)
            }
            val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(requireContext()).load(AppConstance.mediaFile)
                .apply(requestOptions)
                .into(ivPhoto)
        }
        binding.btnPost.setOnClickListener {
//            if (binding.etPostCaption.text.isEmpty()) {
//                Toast.makeText(requireContext(),R.string.enter_a_post_description, Toast.LENGTH_SHORT).show()
//            }
            if (AppConstance!!.mediaFile == null) {
                Toast.makeText(requireContext(),"Upload media.", Toast.LENGTH_SHORT).show()
            }
            else{
                val userUid = getSharedPre().userId
                val filename = System.currentTimeMillis()
                val storageRef = Firebase.storage.reference
                videoRef = storageRef.child("/images_videos/$userUid/$filename")
                    //.child(userUid!!)
                    //.child(filename.toString())
                uploadData(Uri.fromFile(AppConstance!!.mediaFile))

                //
//                val userUid = sharedPre.userId!!
//                val storageRef = Firebase.storage.reference
//                val filename = System.currentTimeMillis()
//                profilePicRef = storageRef.child("/profile_images/$userUid/$filename")
//                uploadData(profileUri)
            }
        }
    }
    private fun uploadData(uri: Uri) {
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
                                    getSharedPre().userId,
                                    curDate,
                                    generatedVideoFilePath!!,
                                    generatedImageFilePath!!
                                )
                            } else {
                                generatedImageFilePath = ""
                                generatedVideoFilePath = task.result.toString()
                               // showCustomAlert("video done",binding.root)
                                createPostAPI(binding.etPostCaption.text.toString(),
                                    getSharedPre().userId,
                                    curDate,
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

    private fun createPostAPI(caption: String,
                              userId: String?,
                              curDate: String?,
                              generatedVideoFilePath: String,
                              generatedImageFilePath: String,)
    {
        viewModel.createPost(caption,userId, curDate,generatedVideoFilePath, generatedImageFilePath,getSharedPre().userCity,
            getSharedPre().userGroupId!!,
            getSharedPre().userState!!,
            postLocation
        )
            .observe(requireActivity(), androidx.lifecycle.Observer {
                when (it.status) {
                    BaseDataSource.Resource.Status.LOADING -> {
                        //loader.show()
                    }
                    BaseDataSource.Resource.Status.SUCCESS -> {
                        loader.dismiss()
                        showCustomAlert(it.data!!.message,binding.root)
                      //  viewModel.switchHomePage2(requireActivity())

                        startActivityWithAnimation(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            ), Appconstants.SLIDE_IN_BOTTOM
                        )
                        requireActivity().finishAffinity()
                    }
                    BaseDataSource.Resource.Status.ERROR -> {
                        loader.dismiss()
                        Toast.makeText(requireContext(),it.data!!.message,Toast.LENGTH_SHORT).show()
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
                    val geocoder = Geocoder(requireContext())
                    try {
                        addressList = geocoder.getFromLocationName(tempName, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (addressList!!.isEmpty()) {
                        Toast.makeText(requireContext(), "Address not found.", Toast.LENGTH_SHORT).show()
                    } else {
                        val address = addressList[0]
                        //searchLocation = LatLng(address.latitude, address.longitude)
                    }
                } else {
                    Toast.makeText(requireContext(), "Please Enter Location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}