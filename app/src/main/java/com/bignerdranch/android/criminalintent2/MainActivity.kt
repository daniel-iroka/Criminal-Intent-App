package com.bignerdranch.android.criminalintent2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

private const val TAG = "MainActivity"



// Implementing Our Callbacks interface
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        /** || MAKING USE OF THE FRAGMENT MANAGER BY USING THE "supportFragmentManager" TO HANDLE THE FRAGMENT FOR US. || **/


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

    // This responds to a button click in our fragment through our interface, it then replaces our current fragment being hosted
    // with the detail part of a Crime
    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)   // addToBackStack() = when a User presses Back, this will take him back to the Crime list
            .commit()
    }

}