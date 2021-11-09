package com.bignerdranch.android.criminalintentChallengeVersion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CrimeListFragment.CallBacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


        // This is our container id where we will be hosting our fragment. We first call our fragmentManager to inform where we will host it
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


        // this is null because there is no fragment with the given container ID. I don't know what this is
        if (currentFragment == null)  {
            val fragment = CrimeListFragment()
            supportFragmentManager
                .beginTransaction()   // This creates an instance of our FragmentTransaction and adds our Container view and fragment to it and commit it
                .add(R.id.fragment_container, fragment)
                .commit()
        }


    }

    // We only pass in a Log message for now
    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)   // addToBackStack() = when a User presses Back, this will take him back to the Crime list
            .commit()

    }
}