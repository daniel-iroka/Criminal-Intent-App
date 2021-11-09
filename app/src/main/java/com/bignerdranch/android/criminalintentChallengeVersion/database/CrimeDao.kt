package com.bignerdranch.android.criminalintentChallengeVersion.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.criminalintentChallengeVersion.Crime
import java.util.*

/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


@Dao // This annotation tells Room that this is a DOA
interface CrimeDao {
    //SSH TEST

    /** Both @Query annotations signify that both functions are pulling out information from the database not deleting or inserting . **/



    // It tells ROOM to pull all columns for all rows in the database
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>   // using LiveData here will signal Room to run a Query request in a background thread which will be sent over to the main thread


    // It tells ROOM to pull all columns for only the row whose id matches the ID value
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun addCrime(crime: Crime)
}