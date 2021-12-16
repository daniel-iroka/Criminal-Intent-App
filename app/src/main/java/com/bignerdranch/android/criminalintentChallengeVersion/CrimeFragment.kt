package com.bignerdranch.android.criminalintentChallengeVersion

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
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/

private const val ARG_CRIME_ID = "crime_id"
const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"

// This is our Fragment which we will use to work on our Fragment's view
// THIS FILE WILL CONTAIN OUR CRIME'S DETAIL
class CrimeFragment : Fragment() {

    private lateinit var crime :Crime
    private lateinit var photoFile : File
    private lateinit var photoUri: Uri
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckedBox: CheckBox
    private lateinit var timePickerButton : Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callSuspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private var imageViewWidth = 0
    private var imageViewHeight = 0


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
        callSuspectButton = view.findViewById(R.id.call_suspect_button) as Button
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

            val showDate = DatePickerFragment.newInstance(crime.date)
            showDate.show(this@CrimeFragment.childFragmentManager, DIALOG_DATE)
        }


        // initializing our timePicker Button to enable the user select a particular time for a date
        timePickerButton.setOnClickListener {

            childFragmentManager.setFragmentResultListener("transferKey", viewLifecycleOwner)  { _, bundle ->

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
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    // we use getUriFile to get the Uri of a file that the camera app can recognize
                    photoUri = FileProvider.getUriForFile(requireActivity(), "com.bignerdranch.android.criminalintentChallengeVersion.fileprovider", photoFile)
                    updateUI()
                    updateTime()
                }
            }
        )
    }


    // function to update UI wherever it is called
    private fun updateUI() {
        val dateLocales = SimpleDateFormat("EEE, MMM dd, yyyy.", Locale.getDefault())
        titleField.setText(crime.title)
        dateButton.text = dateLocales.format(this.crime.date)
        solvedCheckedBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()  // this skips the checkBox animation whenever we load crime
        }
        // Sends our suspect's name to our Button
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }

    // function to the Bitmap into our ImageView
    private fun updatePhotoView()  {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, imageViewWidth, imageViewHeight)
            photoView.setImageBitmap(bitmap)
            photoView.contentDescription = getString(R.string.crime_photo_image_description)
        } else {
            photoView.setImageDrawable(null)
            photoView.contentDescription = getString(R.string.crime_photo_no_image_description)
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

                /** Query handling our Suspect's Phone Number. **/

                // Our phoneNumber's contact id which will be obtained from our previous query
                val phoneContactsId = arrayOf(ContactsContract.CommonDataKinds.Phone._ID)
                val phoneCursorId = contactUri?.let {
                    requireActivity().contentResolver
                        .query(contactUri, phoneContactsId, null, null, null)
                }
                phoneCursorId?.use {
                    if (it.count == 0) return
                    it.moveToFirst()
                    val phoneId = it.getString(0)

                    val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    // Specifies what field we want our query to return which is a Phone Number
                    val phoneQueryField = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phoneWhereClause = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
                    val phoneQueryParameter = arrayOf(phoneId)

                    val phoneCursor = requireActivity().contentResolver
                        .query(phoneUri, phoneQueryField, phoneWhereClause, phoneQueryParameter, null)

                    phoneCursor?.use { cursorPhone ->
                        cursorPhone.moveToFirst()
                        val phoneNumberValue = cursorPhone.getString(0)
                        crime.phoneNumber = phoneNumberValue  // passed our phone NUmber to our property
                    }
                    crimeDetailViewModel.saveCrime(crime)
                }
            }
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }
        }

        // Informing the User that we have taken a picture wen we return to the detail part of the screen
        photoView.postDelayed(Runnable {
            photoView.announceForAccessibility("The Image has been taken")
        }, 5)
    }


    // These are the crime's details we reference through their string resource, since we can't obtain them in runtime
    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)  {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateLocale = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val dateString = dateLocale.format(crime.date)
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }


    // function to updateTime on a crime wherever it is called
    /**
     * Challenge 7 : Localizing Dates
     * **/
    private fun updateTime() {
        val timeLocale = SimpleDateFormat("HH:mm", Locale.getDefault())
        timePickerButton.text = timeLocale.format(crime.time)
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

        // Initializing our photoButton to take pictures of a crime
        photoButton.apply {
            val packageManager = requireActivity().packageManager

            // Will check for activities that correspond to the one in our Intent and disable button if none is found
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false  // will disable button if no corresponding activity is found
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities : List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)  // granting permission to other apps to allow it write Uris to CriminalIntent
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }

        // Initializing our PhotoView to display the full sized image of a crime when clicked
        photoView.setOnClickListener {
            val showImage = ZoomedImageFragment.newInstance(photoFile)
            showImage.show(childFragmentManager, "Zoomed_Picture")
        }

        // Knowing the size of our View before a layout pass happens
        photoView.apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                imageViewWidth = width
                imageViewHeight = height
            }
        }

        // Initializing our callSuspect Button
        callSuspectButton.setOnClickListener {
            val callerIntent = Intent(Intent.ACTION_DIAL).apply {

                val phone = crime.phoneNumber
                data = Uri.parse("tel:$phone")
            }
            startActivity(callerIntent)
        }
    }


    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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