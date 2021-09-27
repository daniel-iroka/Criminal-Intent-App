package com.bignerdranch.android.criminalintent2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

private const val TAG = "CrimeListFragment"

// This is the fragment to display our crime list objects
class CrimeListFragment : Fragment() {


    // We set a ViewModelProvider to provide and instance of CrimeListViewModel and return it whenever the OS requests for a new one.
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }


    // Initializes our Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }


    //Whenever activities call this function, the instance of this fragment will be called
    companion object {
        fun newInstance() : CrimeListFragment {
            return newInstance()
        }
    }
}