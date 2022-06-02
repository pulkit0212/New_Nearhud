package common.neighbour.nearhud.database.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(contact: ContactList)

    @Update
    fun update(contact: ContactList)

    @Delete
    fun delete(contact: ContactList)

    @Query("Select * From ContactList")
    fun GetContactList(): LiveData<List<ContactList>>

    @Query("Select * From ContactList where Name Like '%' || :data || '%' ")
    fun GetContactList(data: String): LiveData<List<ContactList>>

    @Query("Delete From ContactList")
    fun DeleteAllContacts()

    @Query("Delete From ContactList where PhoneNumber=:contact")
    fun DeleteSingleContacts(contact: String)

    @Query("Select * From ContactList where PhoneNumber=:phone")
    fun GetPhone(phone: String): Flow<ContactList>

    @Query("Select * From ContactList where PhoneNumber=:phone")
    fun GetPhoneWithoutFlow(phone: String): ContactList

}