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

enum class RatingDesc (val desc: String) {
    ONESTAR("Not a good photo spot!"),
    TWOSTAR("Mediocre photo spot!"),
    THREESTAR("Good photo spot!"),
    FOURSTAR("Superb photo spot!"),
    FIVESTAR("Best photo spot ever!")
}
class SubmitReviewPopupFragment(var locationTitle: String, var locationSubTitle: String) :
    BottomSheetDialogFragment() {
    companion object {
        const val tag = "SubmitReviewPopupFragment"
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    // Temp Placeholder: Web Call to firebase will be added later.
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
        val view = inflater.inflate(R.layout.bottom_sheet_review, container, false)
        view.findViewById<Button>(R.id.submitReviewButton).setOnClickListener {
            submitReview()
        }
        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.reviewLocationTitle)?.text = locationTitle
        view.findViewById<TextView>(R.id.reviewLocationSubtitle)?.text = locationSubTitle

        view.findViewById<RatingBar>(R.id.reviewRatingBar).setOnRatingBarChangeListener(object :
            RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                if (fromUser) {
                    var ratingScore: RatingDesc? = null
                    val intRating = rating.toInt()
                    when(intRating) {
                        1 -> {
                            ratingScore = RatingDesc.ONESTAR
                        }
                        2 -> {
                            ratingScore = RatingDesc.TWOSTAR
                        }
                        3 -> {
                            ratingScore = RatingDesc.THREESTAR
                        }
                        4 -> {
                            ratingScore = RatingDesc.FOURSTAR
                        }
                        5 -> {
                            ratingScore = RatingDesc.FIVESTAR
                        }
                    }
                    if (ratingScore != null) {
                       view.findViewById<TextView>(R.id.reviewLocationRatingLevelDesc).text = ratingScore.desc
                    }
                }
            }
        })
        return view
    }
}