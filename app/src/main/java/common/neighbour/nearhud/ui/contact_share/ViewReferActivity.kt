package common.neighbour.nearhud.ui.contact_share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import common.neighbour.nearhud.R
import common.neighbour.nearhud.base.BaseActivity
import common.neighbour.nearhud.common.Common
import common.neighbour.nearhud.databinding.ActivityViewReferBinding
import common.neighbour.nearhud.retrofit.model.group.GroupInfo
import common.neighbour.nearhud.retrofit.model.token.ReferedData
import common.neighbour.nearhud.ui.contact_share.adapter.OnReferInterface
import common.neighbour.nearhud.ui.contact_share.adapter.ReferDataAdapter
import common.neighbour.nearhud.ui.login.viewmodel.LoginViewmodel
import common.neighbour.nearhud.ui.registration.RegistrationActivity1
import common.neighbour.nearhud.ui.registration.RegistrationActivity2

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

            backBtnToolbar.setOnClickListener {
                finish()
            }

            referAdapter = ReferDataAdapter(this@ViewReferActivity,this@ViewReferActivity)
            rvReferData.isNestedScrollingEnabled = false
            val layoutManager = LinearLayoutManager(this@ViewReferActivity, LinearLayoutManager.VERTICAL, false)
            rvReferData.layoutManager = layoutManager
            rvReferData.adapter = referAdapter
            referAdapter.setData(Common.referedData)

            cvContinue.setOnClickListener {
                val intent =
                    Intent(this@ViewReferActivity, RegistrationActivity1::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun refer(referedData: ReferedData) {
        //9425315142
        Common.referGrpID = referedData.referredByGroupId
        Common.referGrpName = referedData.referredByGroupName
        Common.isRefer = true

        val intent =
            Intent(this@ViewReferActivity, RegistrationActivity2::class.java)
        startActivity(intent)
    }

}