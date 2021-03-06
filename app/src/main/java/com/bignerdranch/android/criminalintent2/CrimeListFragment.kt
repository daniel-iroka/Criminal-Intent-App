package com.bignerdranch.android.criminalintent2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"
const val DEFAULT_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z"


// This is the fragment to display our crime list objects
// THIS FILE WILL BE THE FRAGMENT TO DISPLAY OUR CRIME LIST
class CrimeListFragment : Fragment() {

    /**
     * Required interface for hosting activities
     * Any Activity hosting this fragment must implement this interface
     */
    // This is our callbacks function
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    // This is our callbacks property that implements CallBacks?
    private var callbacks: Callbacks? = null
    private lateinit var crimeRecyclerView : RecyclerView
    private var adapter : CrimeAdapter? = CrimeAdapter()

    // We set a ViewModelProvider to provide and instance of CrimeListViewModel and return it whenever the OS requests for a new one.
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }


    // This function sets the callbacks property
    // The 'Context' object here refers to an instance of the hosting Activity(MainActivity) which is called when CrimeListFragment is attached to any Activity,
    // which then calls in onAttach after its been "attached"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // this "explicitly" tells our FragmentManager that our fragment needs to receive a call to menu(onCreateOptionsMenu)
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
        // The linearLayoutManager will position the items in the list vertically
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }


    // This is our LiveData Observer. LiveData.observe() object gets notified when the LiveData has been received(in this case a list of crimes).
    // The viewLifecycleOwner is tied to the life of the Activity or Fragment so that the process gets destroyed as the Activity does
    // But in this case it is tied to our Fragment's view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }


    // This function unsets our callbacks property. Its is a lifecycle function
    // This sets the property to null meaning that our Activity is no longer Accessible
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    // This is the callBacks function used to inflate our menu resource layout file in our CrimeListFragment
    // This callBacks is gotten from the fragment or Activity class

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }


    /** NOTE : clicking on our action_item triggers the detailPart through our callBacks interface **/

    // This function is called when an action Item is clicked in an activity or fragment. It then matches our action_item id with the menuItem id
    // and of course the menuItem id is the id defined in our menu file
    // Basically we are describing what we want to happen whenever out action_item is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)  {
            R.id.new_crime -> {

                // When our action_item is clicked, we will add a newCrime to our database and notify our Hosting Activity of a new crime selected
                // through our interface when we press back button our detail part to list part
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true   // this indicates that our menu processing is over
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    // This is a function that connects our adapter to our RecyclerView and populates our UI
    private fun updateUI(crimes: List<Crime>) {
        // We did this because submitList() is a sub-class of ListAdapter not recyclerView
        // so we need to cast superType to subType
        (crimeRecyclerView.adapter as CrimeAdapter).submitList(crimes)
    }


    // This class is where we wire up views in our item list with a ViewHolder
    // A ViewHolder in a RecyclerView makes reference to a view by their "itemViews" or storing them in a property called itemView
    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

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
        // Because the Adapter should know as little as possible the details of the ViewHolder
        fun bind(crime: Crime) {
            // BONUS FEATURE - will format the date according to the device's locale
            val dateLocales = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = dateLocales.format(this.crime.date)

            val isSolved = "The case is solved."
            if (solvedImageView.isVisible) {
                itemView.contentDescription = getString(R.string.crime_brief_summary, crime.title, dateTextView.text, isSolved)
            } else {
                itemView.contentDescription = getString(R.string.crime_summary, crime.title, dateTextView.text)
            }


            // This code here determines the visibility of the ImageView based on an "if" else statement
            // If crime.isSolved == true, show visibility, else, don't show View.GONE
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }


        // Since our ViewHolder implements the OnCLickListener itself, we need to implements its members,
        // In this case, we need to set what will happen when our button is clicked
        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
        }
        }


    /** || IMPORTANT NOTE : The "RecyclerView" does not set data by itself, rather it calls in the Adapter to do it. **/


    // A recyclerView does not create or hold ViewHolders by itself, rather it asks an "Adapter" to do so
    // This class here sets the data the Recycler view will display through the CrimeHolder
    private inner class CrimeAdapter
        : ListAdapter<Crime, CrimeHolder>(CrimeDiffCallBack) {

        // This wraps up the inflated recyclerView layout and passes it to the CrimeHolder creating a new ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

//        No need for this because ListAdapter now handles the list of crimes
//        override fun getItemCount() = crimes.size   // This reveals the number of items in the list of crimes

        // This obtains crimes from a particular position from the crime list and passes it to the CrimeHolder
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(getItem(position))    // passes a particular crime according to its position
        }
    }


    // This implementation will be provided for our ListAdapter
    // This implementation checks the difference between the current list and the changed list in our recyclerView
    object CrimeDiffCallBack : DiffUtil.ItemCallback<Crime>()  {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }
    }


    //Whenever activities call this function, the instance of this fragment will be called
    companion object {

        fun newInstance() : CrimeListFragment {
            return CrimeListFragment()
        }
    }
}