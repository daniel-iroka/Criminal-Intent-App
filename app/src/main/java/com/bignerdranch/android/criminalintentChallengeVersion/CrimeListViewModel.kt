package com.bignerdranch.android.criminalintentChallengeVersion

import androidx.lifecycle.ViewModel


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/



// This is our ViewModel where we will store our list of crimes objects to eventually display on our screen
// THIS FILE WILL HOLD OUR CRIME'S LIST AS A VIEW MODEL TO PRESERVE THE STATE OF OUR UI
class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()


    fun addCrime(crime : Crime) {
        crimeRepository.addCrime(crime)
    }
}