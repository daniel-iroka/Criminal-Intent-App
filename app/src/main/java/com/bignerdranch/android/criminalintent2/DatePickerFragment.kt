package com.bignerdranch.android.criminalintent2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


// This is our DatePickerFragment in which we will implement our DatePickerDialog inside and will be hosted by our Hosting Activity

private const val ARG_DATE = "date"

/** FRAGMENT A **/

class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->

            val resultDate : Date = GregorianCalendar(year, month, day).time

            // This is where we store our result in a Bundle ready to be passed to our other fragment
            val result = Bundle().apply {
                putSerializable("bundleKey", resultDate)
            }
            // sending data to our hosting fragment, so we use the .parentFragmentManager
            parentFragmentManager.setFragmentResult("requestKey", result)
        }


        // Now we will reference or access the date passed from CrimeFragment to our DatePickerFragment fragment's bundle
        //  We also remember that we can only access a value(date) through its key
        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.time = date // getting our dates from the "Calendar" object

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        // The DatePickerDialog is responsible for displaying a prompt interface dialog in which the user will input a date
        // for the current crime. And it takes in three parameters
        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }


    // Here we store our "date" in our fragments bundle so that we can access it later
    companion object {

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}