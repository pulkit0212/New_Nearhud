package common.neighbour.nearhud.ui.contact_share

import android.Manifest
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.core.NewBaseFragment
import common.neighbour.nearhud.databinding.FragmentInvitedBinding
import common.neighbour.nearhud.newUi.ProgressView
import common.neighbour.nearhud.retrofit.model.contact_share.ContactList
import common.neighbour.nearhud.ui.contact_share.adapter.ContactAdapter
import common.neighbour.nearhud.ui.contact_share.adapter.OnSendInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvitedFragment: NewBaseFragment<ContactViewModel,
        FragmentInvitedBinding>(ContactViewModel::class.java),OnSendInterface
{
    private val loader by lazy { ProgressView.getLoader(requireActivity()) }
    lateinit var contactAdapter: ContactAdapter

    override fun getLayoutRes() = R.layout.fragment_invited

    override fun init() {
        super.init()
        binding.lifecycleOwner = this

        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    initObserver()
                    initViews()
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
    }

    private fun initViews() {
        binding.sRefresh.setOnRefreshListener {
            //api
            sortContact()
            binding.sRefresh.isRefreshing = false
        }
    }

    private fun initObserver() {
        viewModel.getContactsList().observe(this) {
            viewModel.list.clear()
            viewModel.contactHashMap.clear()
            for (item in it){
                viewModel.list.add(item.phoneNumber)
                viewModel.contactHashMap[item.phoneNumber] = item.name
            }

            sortContact()
        }
    }


    fun newInstance(): Fragment {
        val f = InvitedFragment()
        return f
    }

    private fun sortContact() {
        viewModel.sortContactList(viewModel.list,getSharedPre().userId!!,).observe(this, Observer {

            when (it.status) {
                BaseDataSource.Resource.Status.LOADING -> {
                    loader.show ()
                }
                BaseDataSource.Resource.Status.SUCCESS -> {
                    loader.dismiss()
                    viewModel.contactList.clear()
                    getContact(it.data!!.data[0].numberInvited)
                    binding.apply {
                        contactAdapter = ContactAdapter(false,requireContext(),this@InvitedFragment)
                        rvContact.isNestedScrollingEnabled = false
                        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        rvContact.layoutManager = layoutManager
                        rvContact.adapter = contactAdapter
                        contactAdapter.setData(viewModel.contactList)
                    }

                }
                BaseDataSource.Resource.Status.ERROR -> {
                    loader.dismiss()
                    Toast.makeText(requireContext(),it.data!!.message, Toast.LENGTH_SHORT).show()
                    //showCustomAlert(it.data!!.message)
                }
            }
        })
    }

    //check hash keys
    fun getContact(numberUnInvited: ArrayList<String>) {
        viewModel.contactList.clear()
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

    override fun send(number: String) {}

}