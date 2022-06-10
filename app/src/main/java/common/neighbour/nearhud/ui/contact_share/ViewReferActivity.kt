package common.neighbour.nearhud.ui.contact_share

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.databinding.ActivityLoginBinding
import common.neighbour.nearhud.databinding.ActivityViewReferBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.retrofit.model.token.ReferedData
import common.neighbour.nearhud.ui.contact_share.adapter.ContactAdapter
import common.neighbour.nearhud.ui.contact_share.adapter.OnReferInterface
import common.neighbour.nearhud.ui.contact_share.adapter.ReferDataAdapter
import common.neighbour.nearhud.ui.login.OtpActivity
import common.neighbour.nearhud.ui.login.viewmodel.LoginNavigator
import common.neighbour.nearhud.ui.login.viewmodel.LoginViewmodel

class ViewReferActivity : BaseActivity(),OnReferInterface {

    private val viewModel: LoginViewmodel by viewModels()
    private lateinit var binding: ActivityViewReferBinding
    private lateinit var context: Context
    lateinit var referAdapter: ReferDataAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_refer)
        context = this
        initViews()
    }

    private fun initViews() {
        binding.apply {
            referAdapter = ReferDataAdapter(this@ViewReferActivity,this@ViewReferActivity)
            rvReferData.isNestedScrollingEnabled = false
            val layoutManager = LinearLayoutManager(this@ViewReferActivity, LinearLayoutManager.VERTICAL, false)
            rvReferData.layoutManager = layoutManager
            rvReferData.adapter = referAdapter
            referAdapter.setData(viewModel.referedData)
        }
    }

    override fun refer(referedData: ReferedData) {

    }

}