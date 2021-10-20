package com.bignerdranch.android.criminalintent2

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"



/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/




// THIS FILE WILL BE THE FRAGMENT TO DISPLAY OUR CRIME LIST
class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView : RecyclerView
    private var adapter : CrimeAdapter? = CrimeAdapter(emptyList())


    // We set a ViewModelProvider to provide and instance of CrimeListViewModel and return it whenever the OS requests for a new one.
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
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


        // A recycler view needs a LayoutManger to work. It is used to position items on the screen itself
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter


        return view
    }


    // This is where we will set our LiveData observer which will notified when the data has been received from the database and is ready
    // to update the UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,  // the viewLifecycleOwner is tied to the Fragment's view which means it will only get executed if the View is in memory
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got a list of ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }


    // This is a function that connects our adapter to our RecyclerView and populates our UI
    private fun updateUI(crimes :List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }



    // A Generic ViewHolder to be implemented by other ViewHolders
    abstract class BaseViewHolder<T>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

        abstract fun bind(crime: Crime)

    }


    // A ViewHolder in a RecyclerView makes reference to a view by their "itemViews" or storing them in a property called itemView
    /** || FIRST VIEW HOLDER || **/
    private inner class CrimeHolder(view: View)
        : BaseViewHolder<Crime>(view), View.OnClickListener {

        private lateinit var crime : Crime

        private val titleTextView : TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView : TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView : ImageView = itemView.findViewById(R.id.crime_solved)


        // We set an onClickListener on each crime represented by their itemViews
        // The itemView is the view for the entire row
        init {
            itemView.setOnClickListener(this)
        }



        // This function is added here so that our ViewHolder will do the binding work of the crimes instead of our TextViews(it is a good practice)
        override fun bind(crime: Crime) {

            /** CHALLENGE 2 : FORMATTING THE DATE - Using string.formatting, use the functions in the DateFormat class to change the format of the date. **/
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("MMM dd, yyyy.", this.crime.date)

            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }

        }


        // Since our ViewHolder implements the OnCLickListener itself, we need to implements its members,
        // In this case, we need to set what will happen when our button is clicked
        override fun onClick(v: View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

    }


    /** CHALLENGE 1 : RECYCLERVIEW VIEW TYPES : Create two types of rows in your recyclerView. A normal row and a row for more serious crimes
     *                Uncomment this line of code to access Challenge **/

    // Second ViewHolder for our serious crime
    /** || SECOND VIEW HOLDER || **/
    /** private inner class SecondViewHolder(view :View)
        : BaseViewHolder<Crime>(view)  {

            private val buttonTextView : Button = itemView.findViewById(R.id.contact_police_button)


        override fun bind(crime: Crime) {


            buttonTextView.apply {
                text = crime.contactPolice
            }
        }
    } **/





    // A recyclerView does not create or hold ViewHolders by itself, rather it asks an "Adapter" to do so
    // This class here sets the data the Recycler view will display through the CrimeHolder
    private inner class CrimeAdapter(var crimes: List<Crime>)
        : RecyclerView.Adapter<BaseViewHolder<*>>() {


        val firstViewType = 0
        val secondViewType = 1



        // This wraps up the inflated recyclerView layout and passes to the CrimeHolder creating a new ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)


            // CHALLENGE CODE SNIPPET

            /** return if (viewType == firstViewType) {
                return CrimeHolder(
                    layoutInflater.inflate(R.layout.serious_crime_layout, parent, false)
                )
            } else {
                SecondViewHolder(
                    layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                )
            } **/



        }

        override fun getItemCount() = crimes.size   // This reveals the number of items in the list of crimes


        // Retrieves a viewType
        override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
            return crime.requiresPolice

        }


        // This obtains crimes from a particular position from the crime list and passes it to the CrimeHolder
        override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }


    }


    //Whenever activities call this function, the instance of this fragment will be called
    companion object {
        fun newInstance() : CrimeListFragment {
            return newInstance()
        }
    }
}