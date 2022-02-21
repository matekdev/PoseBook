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


class MapReviewFragment(var locationTitle: String, var locationSubTitle: String) :
    BottomSheetDialogFragment() {
    companion object {
        const val tag = "SubmitMapReviewPopupFragment"
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
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