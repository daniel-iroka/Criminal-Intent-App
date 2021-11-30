package com.bignerdranch.android.criminalintentChallengeVersion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()   // this represents the ID of the crime about to be displayed by CrimeFragment's UI

    val mainTime = Time()

    // This property retrieves the crime Object from the database to be displayed by Crime's fragment UI according to the passed ID
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    // This function writes a crime to the database when the user inputs in the detail part of the screen
    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    // This exposes our photo's file information to CrimeFragment
    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}