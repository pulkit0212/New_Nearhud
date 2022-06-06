package common.neighbour.nearhud.ui.contact_share

import android.Manifest
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener.Builder.withContext
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener.Builder.withContext
import com.karumi.dexter.listener.single.PermissionListener
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.databinding.ActivityContactBinding
import common.neighbour.nearhud.ui.contact_share.adapter.ContactAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactActivity : NewBaseActivity<ContactViewModel, ActivityContactBinding>()  {

    override fun getViewBinding() = ActivityContactBinding.inflate(layoutInflater)

    lateinit var contactAdapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    viewModel.getContacts()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    showCustomAlert("Need Contact Permission", binding.root)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest, permissionToken: PermissionToken
                ) {
                }
            }).check()

        initObserver()
        initViews()
    }

    private fun initViews() {
        binding.btnFilter.setOnClickListener {

        }
    }

    private fun initObserver() {
        viewModel.getContactsList().observe(this) {
            binding.apply {
                contactAdapter = ContactAdapter(this@ContactActivity)
                rvContact.isNestedScrollingEnabled = false
                val layoutManager = LinearLayoutManager(this@ContactActivity, LinearLayoutManager.VERTICAL, false)
                rvContact.layoutManager = layoutManager
                rvContact.adapter = contactAdapter
                contactAdapter.setData(ArrayList(it.toMutableList()))

                for (item in it){
                    viewModel.list.add(item.phoneNumber)
                }
            }

        }

    }
}