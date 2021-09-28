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

// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL DETAIL OUR CRIMES DETAIL
class CrimeFragment : Fragment() {

    private lateinit var crime :Crime
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox


    /**  || MOST FUNCTIONS USED IN FRAGMENTS ARE LIFECYCLE CALL BACK FUNCTIONS USED TO PERSIST THE STATE OF THE UI. such as below ||  **/

    // This initializes our Activity. Sort of our entry point
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
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
}