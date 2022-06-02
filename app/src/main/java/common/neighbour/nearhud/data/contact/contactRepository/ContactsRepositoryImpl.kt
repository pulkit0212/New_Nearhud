package common.neighbour.nearhud.data.contact.contactRepository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import common.neighbour.nearhud.database.datastore.DataStoreBase
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.database.roomdatabase.AppDB
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ContactsRepositoryImpl @Inject constructor(
    val db: AppDB,
    val context: Context,
    val dataStore: DataStoreBase,
    val sharedPre: SharedPre,
) :
    ContactRepository {

    override suspend fun insertContact(contact: ContactList) = db.getDao().insert(contact)


    override fun getContact(phone: String): Flow<ContactList> {
        return db.getDao().GetPhone(phone)
    }

    override fun getPhoneWithoutFlow(phone: String): ContactList {
        return db.getDao().GetPhoneWithoutFlow(phone)
    }

    override fun deleteAll() {
        GlobalScope.launch {
            db.getDao().DeleteAllContacts()
        }
    }

    override fun deleteSingleContact(contact: ContactList) {
        GlobalScope.launch {
            db.getDao().DeleteSingleContacts(contact.phoneNumber)
        }
    }

    override fun getContactList(): LiveData<List<ContactList>> {
        return db.getDao().GetContactList()
    }

    override fun getContactList(data: String): LiveData<List<ContactList>> {
        return db.getDao().GetContactList(data)
    }

    @SuppressLint("Range")
    override suspend fun loadContact() {

        val resolver: ContentResolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            try {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                var name: String = ""
                var number: String = ""
                while (cursor.moveToNext()) {
                    val hasPhoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt()
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    if (hasPhoneNumber > 0) {
                        name = cursor.getString(nameIndex)
                        val phoneCursor: Cursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf<String>(id),
                            null
                        )!!
                        if (phoneCursor.moveToNext()) {
                            var phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            phoneNumber=getMeMyNumber(phoneNumber)
                            val photo = phoneCursor.getString(
                                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                            )
                            val email = phoneCursor.getString(
                                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                            )
                            if (phoneNumber.length > 10 && phoneNumber.length < 13) {
                                number = phoneNumber.replace("\\s+".toRegex(), "")
                                saveContact(number, name, email, photo?:"", mobileNoSet)
                            } else if (phoneNumber.length == 10) {
                                number = phoneNumber.replace("\\s+".toRegex(), "")
                                saveContact("91" + number, name, email, photo?:"", mobileNoSet)
                            } else {
                                Log.e("RichCall","Nothing")
                            }


                            phoneCursor.close()
                        }

                    }

                }
            } finally {
                cursor.close()
            }
        }
    }

    fun getMeMyNumber(number: String, countryCode: String="91"): String? {
        return number.replace("[^0-9\\+]".toRegex(), "") //remove all the non numbers (brackets dashes spaces etc.) except the + signs
            .replace("(^[1-9].+)".toRegex(), "$countryCode$1") //if the number is starting with no zero and +, its a local number. prepend cc
            .replace("(.)(\\++)(.)".toRegex(), "$1$3") //if there are left out +'s in the middle by mistake, remove them
            .replace("(^0{2}|^\\+)(.+)".toRegex(), "$2") //make 00XXX... numbers and +XXXXX.. numbers into XXXX...
            .replace("^0([1-9])".toRegex(), "$countryCode$1") //make 0XXXXXXX numbers into CCXXXXXXXX numbers
    }
    private suspend fun saveContact(
        number: String,
        name: String,
        email: String,
        photo: String,
        mobileNoSet: HashSet<String>
    ) {
        if (!mobileNoSet.contains(number)) {
            mobileNoSet.add(number)
            val data = getPhoneWithoutFlow(number)
            if (data == null && number.length > 9) {
                if (photo.isNullOrBlank()) {
                    var contact = ContactList(
                        number,
                        name,
                        "",
                        email
                    )
                    insertContact(contact)
                } else {
                    var contact = ContactList(
                        number,
                        name,
                        photo,
                        email
                    )
                    insertContact(contact)
                }

            }
            Log.d(
                "contacts",
                " Phone Number: name = " + name + " No = " + number
            )
        }
    }

//    override fun getMedia(cursor: Cursor?) = flow<MutableList<MediaItem>> {
//        cursor?.let {
//            val mediaList = mutableListOf<MediaItem>()
//            CoroutineScope(Dispatchers.IO).async {
//                val index = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
//                val dateIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
//                val typeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
//                val durationIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
//                val titleIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)
//                val pathIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
//                while (it.moveToNext()) {
//                    val id = cursor.getLong(index)
//                    val path = ContentUris.withAppendedId(MediaConstants.IMAGE_VIDEO_URI, id)
//                    val mediaType = cursor.getInt(typeIndex)
//                    val longDate = cursor.getLong(dateIndex)
//                    val absolutePath = cursor.getString(pathIndex)
//                    val title = cursor.getString(titleIndex)
//                    val duration = cursor.getLong(durationIndex)
//                    //Log.e("Media", "getMedia: $path $longDate $mediaDate $absolutePath $duration")
//                    var type = if (mediaType == 1) {
//                        "IMAGE"
//                    } else {
//                        "VIDEO"
//                    }
//                    if (mediaType == 1) {
//                        mediaList.add(
//                            MediaItem(
//                                id,
//                                path.toString(),
//                                type,
//                                longDate,
//                                absolutePath,
//                                duration,
//                                title
//                            )
//                        )
//                    }
//                }
//            }.await()
//            emit(mediaList)
//        }
//    }
}