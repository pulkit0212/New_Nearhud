package common.neighbour.nearhud.ui.welcome.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import common.neighbour.nearhud.base.BaseFragment
import common.neighbour.nearhud.R
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.FragmentWelcome3Binding
import common.neighbour.nearhud.ui.login.LoginActivity


class WelcomeFragment3 : BaseFragment() {
    private lateinit var binding: FragmentWelcome3Binding

    override fun WorkStation() {
        binding.textView.text = Common.launchData[2].heading
        binding.textView2.text = Common.launchData[2].message
        Glide.with(requireActivity()).load(Common.launchData[2].image).placeholder(R.drawable.welcome3_image).into(binding.imageView)
        AccessClickLIstener()
    }

    private fun AccessClickLIstener() {
   binding.loginButton.setOnClickListener {
       startActivity(Intent(context, LoginActivity::class.java))
   }
//        binding.signUpButton.setOnClickListener {
//            startActivity(Intent(context,CreateAccountActivity::class.java))
//        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_welcome3,
            container,
            false
        )
        WorkStation()
        return binding.root
    }

    companion object {
        private var instance: WelcomeFragment3? = null

        @JvmStatic
        fun newInstance(): WelcomeFragment3? {
            if (instance == null) {
                instance = WelcomeFragment3()
            }
            return instance
        }

    }
}