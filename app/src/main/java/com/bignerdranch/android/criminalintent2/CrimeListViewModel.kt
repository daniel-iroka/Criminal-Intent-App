package com.bignerdranch.android.criminalintent2

import androidx.lifecycle.ViewModel

/** THIS FILE WILL HOLD OUR CRIME'S LIST AS A VIEW MODEL TO PRESERVE THE STATE OF OUR UI **/


class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()  // The get() enabled us to reference it even when private
    val crimeListLiveData = crimeRepository.getCrimes()


    // function to add a new crime to the database with the action item defined in CrimeListFragment
    fun addCrime(crime :Crime) {
        crimeRepository.addCrime(crime)
    }

}