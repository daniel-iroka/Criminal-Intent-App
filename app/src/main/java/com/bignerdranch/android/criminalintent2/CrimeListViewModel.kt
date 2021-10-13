package com.bignerdranch.android.criminalintent2

import androidx.lifecycle.ViewModel


// This is our ViewModel where we will store our list of crimes objects to eventually display on our screen
// THIS FILE WILL HOLD OUR CRIME'S LIST AS A VIEW MODEL TO PRESERVE THE STATE OF OUR UI
class CrimeListViewModel : ViewModel() {

    // A dummy "List" of crimes
    val crimes = mutableListOf<Crime>()

    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0  // this is actually a Boolean Expression. The value of this(i % 2 == 0) will determine if isSolved is "true" or "false"
            crimes += crime
        }
    }
}