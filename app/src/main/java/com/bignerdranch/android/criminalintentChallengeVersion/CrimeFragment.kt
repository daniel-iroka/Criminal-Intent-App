package com.bignerdranch.android.criminalintentChallengeVersion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/




private const val ARG_CRIME_ID = "crime_id"
const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"



// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL CONTAIN OUR CRIME'S DETAIL
class CrimeFragment : Fragment() {

    private lateinit var crime :Crime
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox
    private lateinit var timePickerButton : Button


    /**  || MOST FUNCTIONS USED IN FRAGMENTS ARE LIFECYCLE CALL BACK FUNCTIONS USED TO PERSIST THE STATE OF THE UI. such as below ||  **/


    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    // This initializes our Activity. Sort of our entry point
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()


        val crimeId : UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }



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
        timePickerButton = view.findViewById(R.id.crime_timePicker) as Button





        // implementing the Date Button
        dateButton.setOnClickListener {

            // This is the replacement of "setTargetFragment". We use this function to connect both fragments together
            childFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { Key, bundle ->


                val result = bundle.getSerializable("bundleKey") as Date
                crime.date = result
                updateUI()
            }
            updateUI()


            val showDate = DatePickerFragment.newInstance(crime.date)
            showDate.show(this@CrimeFragment.childFragmentManager, DIALOG_DATE)
        }



        // initializing our timePicker Button to enable the user select a particular time for a date
        timePickerButton.setOnClickListener {

            childFragmentManager.setFragmentResultListener("transferKey", viewLifecycleOwner)  { Key, bundle ->

                val result = bundle.getSerializable("resultKey") as Date
                crime.time = result
                updateTime()
            }
            updateTime()


            val showTime = TimePickerFragment.newInstance(crime.time)
            showTime.show(this@CrimeFragment.childFragmentManager, DIALOG_TIME)
        }


        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                    updateTime()
                }
            }
        )
    }



    // function to update UI wherever it is called
    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = DateFormat.format("EEE, MMM dd, yyyy.", this.crime.date)
        solvedCheckedBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()  // this skips the checkBox animation whenever we load crime
        }
    }


    // function to updateTime on a crime wherever it is called
    private fun updateTime() {
        timePickerButton.text = DateFormat.format("HH:mm", crime.time)
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


    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }


    // Creating an instance of CrimeFragment and receiving our crime
    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}