package common.neighbour.nearhud.ui.contact_share

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseActivity
import common.neighbour.nearhud.databinding.ActivityContactBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.retrofit.model.contact_share.ContactList
import common.neighbour.nearhud.ui.contact_share.adapter.ContactAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactActivity : NewBaseActivity<ContactViewModel, ActivityContactBinding>()  {

    override fun getViewBinding() = ActivityContactBinding.inflate(layoutInflater)
    private val loader by lazy { ProgressView.getLoader(this) }

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
        sortContact()
    }

    private fun sortContact() {
        viewModel.sortContactList(viewModel.list,sharedPre().userId!!,).observe(this, Observer {

            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show ()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    println("SortedList ${it.data?.data}")
                    getContact(it.data!!.data[0].numberUnInvited)
                    println("SortedList ${viewModel.contactList}")
                    binding.apply {
                        contactAdapter = ContactAdapter(this@ContactActivity)
                        rvContact.isNestedScrollingEnabled = false
                        val layoutManager = LinearLayoutManager(this@ContactActivity, LinearLayoutManager.VERTICAL, false)
                        rvContact.layoutManager = layoutManager
                        rvContact.adapter = contactAdapter
                        contactAdapter.setData(viewModel.contactList)
                    }

                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(this,it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        })
    }

    private fun initObserver() {
        viewModel.getContactsList().observe(this) {
            for (item in it){
                viewModel.list.add(item.phoneNumber)
                viewModel.contactHashMap[item.phoneNumber] = item.name
            }

            sortContact()
        }
    }

    //check hash keys
    fun getContact(numberUnInvited: ArrayList<String>) {
        for (l in numberUnInvited){
            matchContact(l)
        }
    }
    fun matchContact(number: String){
        for (h in viewModel.contactHashMap) {
            if (h.key == number) {
                val contact = ContactList(h.value,number)
                viewModel.contactList.add(contact)
            }
        }
    }
}