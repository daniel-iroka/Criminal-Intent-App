package com.bignerdranch.android.criminalintent2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.text.format.DateFormat
import android.widget.*
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL CONTAIN OUR CRIME'S DETAIL

private const val ARG_CRIME_ID = "crime_id"
const val DIALOG_DATE = "DialogDate"
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"

// TODO: LATER, I WILL MAKE SURE I WILL UPDATE THE CHALLENGE VERSION WITH THE CORRECT SOLUTION

/** FRAGMENT B **/

class CrimeFragment : Fragment()   {

    private lateinit var crime :Crime // this crime property represents the USER'S EDITS i.e the crime the USER wrote
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private var imageViewWidth = 0
    private var imageViewHeight = 0


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
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView

        // implementing the Date Button
        dateButton.setOnClickListener {

            // This is the replacement of "setTargetFragment". We use this function to connect both fragments together
            childFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->

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
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    // getUruForFile takes in a file path and translates it to a Uri that the camera app can understand
                    photoUri = FileProvider.getUriForFile(requireActivity(), "com.bignerdranch.android.criminalintent2.fileprovider", photoFile)
                    updateUI()
                }
            }
        )
    }


    // the function to populate our UI
    private fun updateUI() {
        // BONUS FEATURE - will format the date according to the device's locale
        val dateLocales = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
        titleField.setText(crime.title)
        dateButton.text = dateLocales.format(crime.date)
        solvedCheckedBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()  // this skips the checkBox animation whenever we load crime
        }

        if (crime.suspect.isNotEmpty()) {    // Adds the contact(suspect) name to the suspect button
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }

    // This function is to load our bitmap into our ImageVIew
    private fun updatePhotoView()  {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, imageViewWidth, imageViewHeight)
            photoView.setImageBitmap(bitmap)
        }  else {
            photoView.setImageDrawable(null)
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
            requestCode == REQUEST_PHOTO -> {
                // This revokes permission to write to our Uri after a valid result has been received
                requireActivity().revokeUriPermission(photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }
        }
    }


    // This function here is being used to replace the details of a crime at run time using string formatting
    // Because we can't get the details of our crime at runtime, so this is kind of like a dummy data
    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date)
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
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


        // Initializing our reportButton to be able to send a crime report to another activity with the help of Intents
        reportButton.setOnClickListener {

            // The "type" of data we are sending is a "plain Text" which includes the contents of
            // getCrimeReport() and our Crime Report subject
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->

                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }


        // Initializing our suspect button to be able to choose a suspect from our contacts list
        suspectButton.apply {

            val pickerContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickerContactIntent, REQUEST_CONTACT)
            }

            // This code was added here because most Users may not have a contacts app
            // and disables the Button if none is found
            val packageManager : PackageManager = requireActivity().packageManager
            // This will give us info about any contacts app found in our device
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickerContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null)  {
                isEnabled = false  // and this will disable our suspect button if no contacts app is found
            }
        }


        // Initializing our photoButton to allow Users to take the picture of a crime
        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY)

            // This will disable our photoButton if no activity that matches our Intent was found
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                // This checks for cameraActivities matching our Intent
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION  // giving permission to allow other activities write to our Uri
                    )
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }

        }

        // Initializing our PhotoView to display the full sized image of a crime when clicked
        // NOTE: Bonus feature from the challenges section
        photoView.setOnClickListener {
            val showImage = ZoomedImageDialogFragment.newInstance(photoFile)
            showImage.show(childFragmentManager, "ZOOMED_IMAGE")
        }

        // Getting to know the size of the image before a layout pass happens
        photoView.apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                imageViewWidth = width
                imageViewHeight = height
            }
        }
    }


    // The Fragment.onStop() function is called whenever a fragment is no longer in memory, therefore this code below saves
    // the USER crime input to the database whenever he/she leaves CrimeFragment such as pressing the back Button
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        // ensures no invalid response was received since we called it on onDetach()
        requireActivity().revokeUriPermission(photoUri,
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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

