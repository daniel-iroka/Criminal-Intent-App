package com.bignerdranch.android.criminalintent2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*

// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL CONTAIN OUR CRIME'S DETAIL

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"

// TODO : OPTIONAL : VISIT INTENTS IN ACTIVITIES.....


class CrimeFragment : Fragment() {


    private lateinit var crime :Crime
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox


    // Providing an instance of CrimeDetailViewModel
    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    /**  || MOST FUNCTIONS USED IN FRAGMENTS ARE LIFECYCLE CALL BACK FUNCTIONS USED TO PERSIST THE STATE OF THE UI. such as below ||  **/

    // This initializes our Activity. Sort of our entry point
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()


        // This is how we pull or reference our fragments arguments passed from the hosting Activity, which is similar to "intents"
        // We remember that we can only reference a value by its "key" in a key-value pair, so we use ARG_CRIME_ID
        val crimeId : UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)    // we then connect the loaded crime from our CrimeDetailViewModel to our CrimeFragment
        // todo : Implement Lifecycle.Observer


    }


    // This is the function used to inflate the fragment_crime.xml layout provided with all the necessary parameters
    // Alternatively can be done by "LayoutInflater.inflate(R.layout.fragment_crime.xml)"
    // This is where we do all the buttons and TextViews
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // The first parameter is the View's ID, second is the View's parent and third is if the inflated view will be immediately
        // added to the View's parent
        val view = inflater.inflate(R.layout.fragment_crime, container, false)


        // Implementing their view by Id in Fragments
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckedBox = view.findViewById(R.id.crime_solved) as CheckBox


        // implementing the Date Button
        dateButton.apply {
            text = crime.date.toString()  // The Date() will set the current date in the button for each crime
            isEnabled = false     // this disables the button
        }

        return view
    }

    // Listener for the EditText and other button
    override fun onStart() {
        super.onStart()


        // TextWatcher class is used to monitor or watch user input text fields and update date on it or other things at the same time
        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space is left intentionally blank
            }

            override fun onTextChanged(sequence: CharSequence?,
            start:Int,
            before:Int,
            count:Int
            ) {
                crime.title = sequence.toString()  // This is the user's input which is the Crime's title and is triggered by the onStart when the user keys in an input
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This space is left intentionally blank
            }
        }

        // This updates the title field with the title the User inputs as an EdiText
        titleField.addTextChangedListener(titleWatcher)



        // I honestly don't understand what this does
        solvedCheckedBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

    }


    // Here is where we are preparing our fragment arguments,create an instance of the CrimeFragment and bundle the received data to our fragment
    // The arguments are characterized by key-value pairs
    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {  // This is where we attach our "arguments" to our Fragment
                arguments = args
            }
        }
    }
}

