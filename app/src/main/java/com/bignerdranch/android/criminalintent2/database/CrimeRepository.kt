package com.bignerdranch.android.criminalintent2.database

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.criminalintent2.Crime
import java.util.*

// todo : WHEN I COME NEXT, I WILL GO TO TESTING QUERIES....

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
    ).build()

    private val crimeDao = database.crimeDao()    // stores references to our DAO objects


    // We added this here so that other components can perform operations on our database
    fun getCrimes(): List<Crime> = crimeDao.getCrimes()

    fun getCrime(id: UUID): Crime? = crimeDao.getCrime(id)



    // This is what makes our entire class a singleton
    companion object {
        private var INSTANCE: CrimeRepository? = null


        // This function initializes our repository
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?:   // this will throw and illegalStateException if our "INSTANCE" has not been created
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}