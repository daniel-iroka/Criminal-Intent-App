package com.bignerdranch.android.criminalintentChallengeVersion

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.DateFormat.getTimeInstance
import java.text.SimpleDateFormat
import java.util.*


/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/

private const val ARG2_DATE = "time"

class TimePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour, minute ->


            // Sending the date back to DatePickerFragment
            val time = arguments?.getSerializable(ARG2_DATE) as Date
            val calendar = Calendar.getInstance()
            calendar.time = time


            val year = calendar.get(Calendar.YEAR)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)


            val mainResult = GregorianCalendar(year, day, month, hour, minute).time


            // Stashing our result in a bundle
            val result = Bundle().apply {
                putSerializable("resultKey", mainResult)
            }

            parentFragmentManager.setFragmentResult("transferKey", result)

        }

        // INITIAL DATE
        val cal = Calendar.getInstance()
        val initialHour = cal.get(Calendar.HOUR_OF_DAY)
        val initialMinute = cal.get(Calendar.MINUTE)



        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }


    // We are preparing our companion object to receive back the date into TimePickerFragment
    companion object {

        fun newInstance(time: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG2_DATE, time)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}