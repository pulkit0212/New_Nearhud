package common.neighbour.nearhud.database.roomdatabase.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactList(@PrimaryKey(autoGenerate = false)
                       var phoneNumber:String,
                       @ColumnInfo var name: String="",
                       @ColumnInfo var profile: String="",
                       @ColumnInfo var email: String=""
                       )
