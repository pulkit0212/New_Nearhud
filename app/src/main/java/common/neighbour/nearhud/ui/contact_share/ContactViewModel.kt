package common.neighbour.nearhud.ui.contact_share

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val repositoryImpl: ContactRepository
) : ViewModel() {

    var list = ArrayList<String>()

    fun getContacts() = viewModelScope.launch(Dispatchers.IO) { repositoryImpl.loadContact() }

    fun getContactsList(): LiveData<List<ContactList>> = repositoryImpl.getContactList()

}