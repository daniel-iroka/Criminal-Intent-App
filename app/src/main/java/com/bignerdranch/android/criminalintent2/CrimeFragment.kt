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
const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"



/** FRAGMENT B **/


class CrimeFragment : Fragment()   {


    private lateinit var crime :Crime // this crime property represents the USER'S EDITS i.e the crime the USER wrote
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox
    private lateinit var timePickerButton : Button


    // Providing an instance of CrimeDetailViewModel
    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    // This initializes our Activity. Sort of our entry point
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()



        // This is how we pull or reference our fragments arguments passed from the hosting Activity, which is similar to "intents"
        // We remember that we can only reference a value by its "key" in a key-value pair, so we use ARG_CRIME_ID
        val crimeId : UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)    // we then connect the loaded crime from our CrimeDetailViewModel to our CrimeFragment

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
        dateButton.setOnClickListener {

            // This is the replacement of "setTargetFragment". We use this function to connect both fragments together
            childFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { Key, bundle ->

                val result = bundle.getSerializable("bundleKey") as Date
                crime.date = result
                updateUI()
            }
            updateUI()

            // This is a way we handle our DatePickerFragment just like all fragments handled by a fragmentManager
            // this@CrimeFragment.childFragmentManager references our DatePickerFragment and .childFragmentManager represents
            // the FragmentManager managing that fragment which is a child of "CrimeFragment"

            val showDate = DatePickerFragment.newInstance(crime.date)
            showDate.show(this@CrimeFragment.childFragmentManager, DIALOG_DATE)
        }


        return view

    }




    // Here we have set a lifecycle Observer to notify us when a crime has been retrieved from our database
    // and this returns a list of our database crimes
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }


    // the function to populate our UI
    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckedBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()  // this skips the checkBox animation whenever we load crime
        }
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



        // this code is for our checkBox and makes it checkable just as how an OnClickListener makes a
        // button clickable
        solvedCheckedBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

    }


    // The Fragment.onStop() function is called whenever a fragment is no longer in memory, therefore this code below saves
    // the USER crime input to the database whenever he/she leaves CrimeFragment such as pressing the back Button
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }


    // Here is where we are preparing our fragment arguments, creating an instance of the CrimeFragment and bundle, then received data to our fragment
    // The arguments are characterized by key-value pairs and are where our received data is stored
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

