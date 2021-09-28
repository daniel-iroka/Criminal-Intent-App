package com.bignerdranch.android.criminalintent2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** || MAKING USE OF THE FRAGMENT MANAGER BY USING THE "supportFragmentManager" TO HANDLE THE FRAGMENT FOR US. || **/


        // This is our container id where we will be hosting our fragment. We first call our fragmentManager to inform where we will host it
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


        // this is null because there is no fragment with the given container ID. I don't know what this is
        if (currentFragment == null)  {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()   // This creates an instance of our FragmentTransaction and adds our Container view and fragment to it and commit it
                .add(R.id.fragment_container, fragment)
                .commit()
        }

    }
}