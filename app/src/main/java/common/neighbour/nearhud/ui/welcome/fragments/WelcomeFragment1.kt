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
import common.neighbour.nearhud.databinding.FragmentWelcome1Binding


class WelcomeFragment1 : BaseFragment() {
    private lateinit var binding: FragmentWelcome1Binding

    override fun WorkStation() {
        binding.textView.text = Common.launchData[0].heading
        binding.tvMessage.text = Common.launchData[0].message
        Glide.with(requireActivity()).load(Common.launchData[0].image).placeholder(R.drawable.welcome1_image).into(binding.imageView)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.fragment_welcome1, container, false)
        WorkStation()
        return binding.root
    }

    companion object {
        private  var instance:WelcomeFragment1?=null
        @JvmStatic
        fun newInstance():WelcomeFragment1?{
           if(instance==null){
               instance=WelcomeFragment1()
           }
            return instance
        }

    }
}