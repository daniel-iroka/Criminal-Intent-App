package com.bignerdranch.android.criminalintent2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent2.Crime

/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


// Defining an abstract class here means that it cannot be directly instantiated, rather we will need to subclass it which keeps it safe

/** CHALLENGE 3 : ADDRESSING THE SCHEMA WARNING - A database schema represents the structure of a database. Address the warning by setting the export
 *                to false. **/
@Database(entities = [ Crime::class], version=1, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {


    // Hooking up our DAO to our Database
    abstract fun crimeDao() : CrimeDao
}