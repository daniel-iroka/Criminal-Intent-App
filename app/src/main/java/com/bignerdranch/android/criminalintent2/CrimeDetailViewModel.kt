package com.bignerdranch.android.criminalintent2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*


// TODO : GO THROUGH USING LIVE DATA TRANSFORMATIONS AGAIN....

class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()   // this represents the ID of the crime about to be displayed by CrimeFragment


    // This property retrieves the crime Object from the database to be displayed by Crime's fragment UI according to the passed ID
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }
}