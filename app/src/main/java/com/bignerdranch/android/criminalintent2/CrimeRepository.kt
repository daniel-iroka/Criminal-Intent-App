package com.bignerdranch.android.criminalintent2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintent2.database.CrimeDatabase
import com.bignerdranch.android.criminalintent2.database.migration_1_4
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"  // stores references to our database

// Our CrimeRepository is responsible for fetching and storing data in a local database or remote server
// This class is a singleton which means it can only instantiated once upon creation and it is so because of the companion object
class CrimeRepository private constructor(context: Context) {


    // Room.databaseBuilder creates a concrete Implementation of our CrimeDatabase using three parameters(simply put it sets up our database)
    // first = is a context Object and we use our singleton
    // second = is the database we want "Room" to create for us
    // third = is the name of the database file we want Room to create for us

    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    )
        .addMigrations(migration_1_4)
        .build()

    private val crimeDao = database.crimeDao()    // stores references to our DAO objects


    // An Executor is an Object that references a thread and performs operations on that thread we specify
    // this below holds reference to "Executors" and defines a "newThread" where we want to perform operations
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir  // returns a handle(use, interaction) to the directory of a private file


    // We added this here so that other components can perform operations on our database
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    
    /** Below is where we Implement our functions with the "executor" object which then performs operations in a background thread. **/
    fun updateCrime(crime: Crime)  {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime)  {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    // This function provides the full local path to a Crime's Image
    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)



    // This is what makes our entire class a singleton
    companion object {
        private var INSTANCE: CrimeRepository? = null


        // This function initializes a new instance of our repository
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        // This function access our repository(INSTANCE)
        fun get(): CrimeRepository {
            return INSTANCE ?:   // this will throw and illegalStateException if our "INSTANCE" has not been created
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}