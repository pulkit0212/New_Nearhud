package common.neighbour.nearhud.ui.camera

import android.net.Uri
import android.util.Log
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import common.neighbour.nearhud.core.extensions.replaceFragment
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import common.neighbour.nearhud.R
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.core.extensions.gone
import common.neighbour.nearhud.core.extensions.likeDoubleTap
import common.neighbour.nearhud.core.extensions.toDateAndTimeString
import common.neighbour.nearhud.ui.home.viewmodel.MainViewModel
import common.neighbour.nearhud.databinding.FragmentClickedPhotoBinding
import common.neighbour.nearhud.repositories.constance.AppConstance
import kohii.v1.exoplayer.Kohii
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class ClickedPhotoFragment : NewBaseFragment<MainViewModel, FragmentClickedPhotoBinding>(
    MainViewModel::class.java){

    override fun getLayoutRes() = R.layout.fragment_clicked_photo

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initializeView()
    }


    fun initializeView() {
        if (AppConstance!!.postType == "image") {
            Log.d("TYPE1",AppConstance.postType)
            Log.d("MEDIA",AppConstance.mediaFile!!.absolutePath)
            setupForImage(AppConstance.mediaFile)
        } else {
            Log.d("TYPE2",AppConstance.postType)
            setupForVideo(AppConstance.mediaFile)
        }
        if (viewModel!!.postIsFromCamera) {
            if (AppConstance!!.postType == "image") {
                binding.imageContainer.doOnLayout {
                    it.handler.postDelayed({
                        //viewModel.anonymousClickedImageBitmap = it.drawToBitmap()
                        binding.postPicAddress.text = viewModel!!.address
                        val formattedDateAndTime = System.currentTimeMillis().toDateAndTimeString()
                        binding.postDateAndTime.text = formattedDateAndTime
                        lifecycleScope.launch(Dispatchers.Main.immediate) {
                            delay(500)
                            viewModel!!.clickedImageBitmap = it.drawToBitmap()
                        }
                    }, 500)
                }
            }
        }
        binding.cutBtn.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(  R.anim.slide_down,
                R.anim.slide_out_down)
        }
        binding.cvNext.setOnClickListener {
            if (requireActivity().supportFragmentManager.findFragmentById(R.id.container) !is CreateMediaPostFragment){
                requireActivity().replaceFragment(CreateMediaPostFragment(), R.id.container, "frag_create_post")
            }
        }


    }

    private fun setupForImage(mediaFile: File?) {
        binding.videoView.gone()
        Glide.with(requireContext())
            .load(AppConstance.mediaFile)
            .centerCrop()
            .into(binding.capturedImage)
    }

    private fun setupForVideo(mediaFile: File?) {
        binding.capturedImage.gone()

        Log.d("TYPE3",AppConstance.postType)
        val dataSpec = DataSpec(Uri.fromFile(mediaFile))
        val fileDataSource = FileDataSource()
        fileDataSource.open(dataSpec)

        val kohii = Kohii[this]
        kohii.register(this).addBucket(binding.root)
        kohii.setUp(Uri.fromFile(mediaFile)).bind(binding.videoView)

        binding.videoView.setOnClickListener {
            binding.muteUnmute.likeDoubleTap()
        }
    }


}