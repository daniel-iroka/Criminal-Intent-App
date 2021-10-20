package com.bignerdranch.android.criminalintent2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalintent2.Crime
import java.util.*



// This file is our DATA ACCESS OBJECTS. It is an interface that contains functions which helps us to carry out operations in our database such as interact
// with the contents of our database.
// In this case, we will only carry two operations needing only two functions. One is toe return a list of all crimes in the database
// AND Two, to return a single crime according to a given matching uuid(id).

@Dao // This annotation tells Room that this is a DOA
interface CrimeDao {
    //SSH TEST

    /** Both @Query annotations signify that both functions are pulling out information from the database not deleting or inserting . **/


    //@Query here takes in SQL commands as parameters
    // It tells ROOM to pull all columns for all rows in the database
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>   // using LiveData here will signal Room to run a Query request in a background thread which will be sent over to the main thread


    // It tells ROOM to pull all columns for only the row whose id matches the ID value
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>
}