package com.bignerdranch.android.criminalintentChallengeVersion

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintentChallengeVersion.database.CrimeDatabase
import com.bignerdranch.android.criminalintentChallengeVersion.database.migration_4_5
import java.util.*
import java.util.concurrent.Executors


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/



private const val DATABASE_NAME = "crime-database"  // stores references to our database


// Our CrimeRepository is responsible for fetching and storing data in a local database or remote server
class CrimeRepository private constructor(context: Context) {


    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_4_5).build()

    private val crimeDao = database.crimeDao()    // stores references to our DAO objects

    private val executor = Executors.newSingleThreadExecutor()



    // We added this here so that other components can perform operations on our database
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)


    // function to update a crime
    fun updateCrime(crime: Crime)  {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }


    // function to add a crime
    fun addCrime(crime: Crime)  {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }




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