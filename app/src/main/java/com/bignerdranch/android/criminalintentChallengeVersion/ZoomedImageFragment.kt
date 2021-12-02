package com.bignerdranch.android.criminalintentChallengeVersion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
/**
 *  CHALLENGE ... : DETAIL DISPLAY
 * **/

class ZoomedImageFragment: DialogFragment() {
    lateinit var fullSizedImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.zoomed_image, container, false)

        fullSizedImage = view.findViewById(R.id.zoomed_imageView) as ImageView


        // just in case we want to reference our sent Image again
        val imageResult = arguments?.getSerializable("Zoomed_Picture") as File

        val bitmap = getScaledBitmap(imageResult.path, requireActivity())
        fullSizedImage.setImageBitmap(bitmap)

        return view
    }

    companion object {

        fun newInstance(imageFileName: File): ZoomedImageFragment {
            val result = Bundle().apply {
                putSerializable("Zoomed_Picture", imageFileName)
            }
            return ZoomedImageFragment().apply {
                arguments = result
            }
        }
    }
}