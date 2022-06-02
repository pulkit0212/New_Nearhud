package common.neighbour.nearhud.data.contact.contactRepository

import androidx.lifecycle.LiveData
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun insertContact(contact: ContactList)
    fun getContact(phone:String): Flow<ContactList>
    fun getPhoneWithoutFlow(phone:String): ContactList
    fun deleteAll()
    fun deleteSingleContact(contact:ContactList)
    fun getContactList(): LiveData<List<ContactList>>
    fun getContactList(data:String): LiveData<List<ContactList>>
    suspend fun loadContact()
//    fun getMedia(cursor: Cursor?): Flow<MutableList<MediaItem>>
//    suspend fun getOtherUserId(number: String): MutableLiveData<UserAccessData>
//    suspend fun saveSenderData(data: RichCallData): MutableLiveData<String>

}