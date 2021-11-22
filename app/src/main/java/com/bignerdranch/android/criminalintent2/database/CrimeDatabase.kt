package com.bignerdranch.android.criminalintent2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent2.Crime


// The @Database annotation tells "Room" that this class is a Database in our app. The annotation also comes with two parameters
// The first parameter signifies the Entities we have created to be used by our database when creating table structures for the database itself
// And the last parameter signifies the version of our database, in this case 1 because we have only one entity or property or one version of the database for now

// Defining an abstract class here means that it cannot be directly instantiated, rather we will need to subclass it which keeps it safe
@Database(entities = [ Crime::class], version=4, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {


    // Hooking up our DAO to our Database
    abstract fun crimeDao() : CrimeDao
}

val migration_1_4 = object : Migration(1,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}