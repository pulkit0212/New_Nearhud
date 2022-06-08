package common.neighbour.nearhud.ui.contact_share

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import common.neighbour.nearhud.application.MyApplication.Companion.repository
import common.neighbour.nearhud.core.BaseViewModel
import common.neighbour.nearhud.data.contact.contactRepository.ContactRepository
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    val sharedPre: SharedPre,
    val repositoryImpl: ContactRepository,
    app: Application
) : BaseViewModel(app) {

    var list = ArrayList<String>()
    var contactList = ArrayList<common.neighbour.nearhud.retrofit.model.contact_share.ContactList>()
    var contactHashMap = HashMap<String,String>()

    fun sortContactList(phoneNb: ArrayList<String>, userId: String)
    = repository.sortContactList(phoneNb,userId)

    fun referNumber(referedById: String, referedByGroupId: String , phoneNo: String
                    ,invitationDate: String, referedByGroupName: String)
    = repository.referNumber(referedById,referedByGroupId,phoneNo,invitationDate,referedByGroupName)

    fun getContacts() = viewModelScope.launch(Dispatchers.IO) { repositoryImpl.loadContact() }

    fun getContactsList(): LiveData<List<ContactList>> = repositoryImpl.getContactList()

}