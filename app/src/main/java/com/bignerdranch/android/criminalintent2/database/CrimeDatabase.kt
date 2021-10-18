package com.bignerdranch.android.criminalintent2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent2.Crime


// The @Database annotation tells "Room" that this class is a Database in our app. The annotation also comes with two parameters
// The first parameter signifies the Entities we have created to be used by our database when creating table structures for the database itself
// And the last parameter signifies the version of our database, in this case 1 because we have only one database for now

// Defining an abstract class here means that it cannot be directly instantiated, rather we will need to subclass it which keeps it safe
@Database(entities = [ Crime::class], version=1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {


    // Hooking up our DAO to our Database
    abstract fun crimeDao() : CrimeDao
}