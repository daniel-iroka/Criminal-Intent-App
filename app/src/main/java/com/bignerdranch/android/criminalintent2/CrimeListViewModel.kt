package com.bignerdranch.android.criminalintent2

import androidx.lifecycle.ViewModel


// TODO When I come back next, I will continue to VIEW MODEL LIFE CYCLE WITH FRAGMENTS...


// This is our ViewModel where we will store our list of crimes objects to eventually display on our screen
class CrimeListViewModel : ViewModel() {

    // A dummy "List" of crimes
    val crimes = mutableListOf<Crime>()

    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0
            crimes += crime
        }
    }
}