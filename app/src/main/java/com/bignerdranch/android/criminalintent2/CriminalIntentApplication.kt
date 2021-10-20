package com.bignerdranch.android.criminalintent2

import android.app.Application


/** NOTE : The purpose of this file and its workings here is to prepare our CrimeRepository to be ready for use upon creation of
 *         CriminalIntent App. **/

// This file is similar to our Activity.onCreate() and is called whenever our application is first loaded into memory
// This can be used to access lifeCycle information
class CriminalIntentApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}