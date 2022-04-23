package com.bignerdranch.android.criminalintent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File

// Bonus feature from our Challenges Section

class ZoomedImageDialogFragment: DialogFragment() {
    private lateinit var fullSizedImage : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.zoomed_image, container, false)

        fullSizedImage = view.findViewById(R.id.zoomed_imageView) as ImageView

        val imageResult = arguments?.getSerializable("ZOOMED_IMAGE") as File

        // Setting our image bitmap
        val bitmap = getScaledBitmap(imageResult.path, requireActivity())
        fullSizedImage.setImageBitmap(bitmap)

        return view
    }

    companion object {

        fun newInstance(picture: File): ZoomedImageDialogFragment {
            val result = Bundle().apply {
                putSerializable("ZOOMED_IMAGE", picture)
            }
            return ZoomedImageDialogFragment().apply {
                arguments = result
            }
        }
    }
}