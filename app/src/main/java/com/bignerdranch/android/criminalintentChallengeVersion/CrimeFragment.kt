package com.bignerdranch.android.criminalintentChallengeVersion

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
private const val REQUEST_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"




// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL CONTAIN OUR CRIME'S DETAIL
class CrimeFragment : Fragment() {

    private lateinit var crime :Crime
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox
    private lateinit var timePickerButton : Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button


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
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button





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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = contactUri?.let {
                    requireActivity().contentResolver
                        .query(it, queryFields, null, null, null)
                }
                cursor?.use {
                    // Verify cursor contains at least one result
                    if (it.count == 0) {
                        return
                    }
                    // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
        }
    }



    // These are the crime's details we reference through their string resource, since we can't obtain them in runtime
    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)  {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
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


        // Initializing our report button to allow the User send a current crime report to another Activity through intents
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }


        // Initializing our suspect button to be able to pick a suspect from our contacts App
        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }


            // This code in general searches for a contacts app that matches the one in our given Intent and retrieves info
            // on it.
            val packageManager : PackageManager = requireActivity().packageManager
            val resolvedActivity : ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null)  {   // However, if it doesn't find any, It'll disable the suspect Button
                isEnabled = false
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