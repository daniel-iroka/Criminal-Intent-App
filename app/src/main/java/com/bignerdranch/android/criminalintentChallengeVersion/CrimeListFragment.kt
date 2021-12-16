package com.bignerdranch.android.criminalintentChallengeVersion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"

/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


// THIS FILE WILL BE THE FRAGMENT TO DISPLAY OUR CRIME LIST
class CrimeListFragment : Fragment() {


    // Our CallBacks interface which will allow us to call functions on our Hosting Activity
    interface CallBacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: CallBacks? = null  // A property holding reference to our CallBacks
    private lateinit var crimeRecyclerView : RecyclerView
    private var adapter : CrimeAdapter? = CrimeAdapter()
    private lateinit var addCrimeButton : Button
    private lateinit var addCrimeText : TextView


    // We set a ViewModelProvider to provide and instance of CrimeListViewModel and return it whenever the OS requests for a new one.
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // We are explicitly telling our fragmentManager that CrimeListFragment needs to receive a call from onCreateOptions....
    }


    // This function sets our callbacks property and is called
    // whenever a fragment is connected or "attached" to an activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as CallBacks?
    }

    // We call this callback function from our Activity to inflate our
    // fragment with our menu layout_file
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    // This callbacks function is called when an action_item or menu_item has been selected
    // We then describe what we want to happen when it is selected which is to add a new crime

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)  // add a new crime to the database
                callbacks?.onCrimeSelected(crime.id) // inform our hosting Activity that a new crime has been selected
                true
            }
            else -> return onOptionsItemSelected(item)
        }
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
        addCrimeButton = view.findViewById(R.id.add_crime_button) as Button
        addCrimeText = view.findViewById(R.id.add_crime_text) as TextView

        // initializing our add crime Button
        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }
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

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    /** Challenge 6 - An Empty View for the RecyclerView **/

    // This is a function that connects our adapter to our RecyclerView and populates our UI
    private fun updateUI(crimes :List<Crime>) {
        if(crimes.isEmpty()) {
            addCrimeButton.isVisible = true
        } else {
            (crimeRecyclerView.adapter as CrimeAdapter).submitList(crimes)
        }
        // Making the textView also disappear
        addCrimeText.visibility = if (crimes.isEmpty()) View.VISIBLE else View.GONE
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
        private val timeTextView : TextView = itemView.findViewById(R.id.time_textView)

        init {
            itemView.setOnClickListener(this)
        }


        // This function is added here so that our ViewHolder will do the binding work of the crimes instead of our TextViews(it is a good practice)
        override fun bind(crime: Crime) {
            /** CHALLENGE 2 : FORMATTING THE DATE - Using string.formatting, use the functions in the DateFormat class to change the format of the date. **/
            val dateLocales = SimpleDateFormat("EEEE, MMM dd, yyyy.", Locale.getDefault())
            val timeLocales = SimpleDateFormat("HH:mm", Locale.getDefault())
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = dateLocales.format(this.crime.date) // Challenge: Localizing Dates
            timeTextView.text = timeLocales.format(this.crime.time)

            val isSolved = "The case is solved"
            if (solvedImageView.isVisible) {
                itemView.contentDescription = getString(R.string.crime_brief_summary, crime.title, dateTextView.text, crime.time, isSolved)
            } else {
                itemView.contentDescription = getString(R.string.crime_summary, crime.title, dateTextView.text , crime.time)
            }

            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
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

    // RecyclerView.Adapter<BaseViewHolder<*>>()

    // This class here sets the data the Recycler view will display through the CrimeHolder
    private inner class CrimeAdapter
        : ListAdapter<Crime, BaseViewHolder<*>>(CrimeDiffCallBack) {

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

        // Retrieves a viewType
        // CHALLENGE CODE SNIPPET
        /**override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
            return crime.requiresPolice(crime)
        } **/

        // This obtains crimes from a particular position from the crime list and passes it to the CrimeHolder
        override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
            holder.bind(getItem(position))
        }
    }


    // This class implementation checks the difference between the new lists and changed lists in our recyclerView
    // Which will be provided for our ListAdapter
    object CrimeDiffCallBack : DiffUtil.ItemCallback<Crime>()  {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id  // assigns new list and old list
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }
    }


    //Whenever activities call this function, the instance of this fragment will be called
    companion object {
        fun newInstance() : CrimeListFragment {
            return newInstance()
        }
    }
}