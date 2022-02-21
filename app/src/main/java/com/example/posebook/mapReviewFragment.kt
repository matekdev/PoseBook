package com.example.posebook

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class mapReviewFragment(var locationTitle: String, var locationSubTitle: String) :
    BottomSheetDialogFragment() {
    companion object {
        const val tag = "SubmitReviewPopupFragment"
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }


    private fun submitReview() {
        dismiss()
        val toast = Toast.makeText(context, "Review Submitted", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_location_viewer_review_template, container, false)
        return view
    }
}