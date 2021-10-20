package com.bignerdranch.android.criminalintent2

import androidx.lifecycle.ViewModel


// This is our ViewModel where we will store our list of crimes objects to eventually display on our screen
// THIS FILE WILL HOLD OUR CRIME'S LIST AS A VIEW MODEL TO PRESERVE THE STATE OF OUR UI
class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()  // The get() enabled us to reference it even when private
    val crimeListLiveData = crimeRepository.getCrimes()
}