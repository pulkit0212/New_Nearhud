package common.neighbour.nearhud.ui.welcome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import common.neighbour.nearhud.base.BaseFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.FragmentWelcome2Binding


class WelcomeFragment2 : BaseFragment() {
    private lateinit var binding: FragmentWelcome2Binding

    override fun WorkStation() {
        binding.textView.text = Common.launchData[1].heading
        binding.tvMessage.text = Common.launchData[1].message
        Glide.with(requireActivity()).load(Common.launchData[1].image).placeholder(R.drawable.welcome2_image).into(binding.imageView)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.fragment_welcome2, container, false)
        WorkStation()
        return binding.root
    }

    companion object {
        private  var instance:WelcomeFragment2?=null
        @JvmStatic
        fun newInstance():WelcomeFragment2?{
           if(instance==null){
               instance=WelcomeFragment2()
           }
            return instance
        }

    }
}