package com.bignerdranch.android.criminalintent2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

// TODO WHEN I COME BACK, I WILL GO TO "Creating an Item View Layout"

// This is the fragment to display our crime list objects
// THIS FILE WILL BE THE FRAGMENT TO DISPLAY OUR CRIME LIST
class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView : RecyclerView


    // We set a ViewModelProvider to provide and instance of CrimeListViewModel and return it whenever the OS requests for a new one.
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }


    // Initializes our Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }


    // This inflates the layout, setting up all the views
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)


        //  finding views by their IDs
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView

        // A recycler view needs a LayoutManger to work. It used to position items on the screen itself
        // The linearLayoutManager will position the items in the list vertically
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }


    //Whenever activities call this function, the instance of this fragment will be called
    companion object {
        fun newInstance() : CrimeListFragment {
            return newInstance()
        }
    }
}