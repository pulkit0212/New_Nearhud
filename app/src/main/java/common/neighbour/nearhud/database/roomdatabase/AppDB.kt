package common.neighbour.nearhud.database.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import common.neighbour.nearhud.database.roomdatabase.tables.ListDB

@Database(entities = [ListDB::class,ContactList::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun getDao(): MyDao
}