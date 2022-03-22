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
import com.example.posebook.manager.CityLocation
import com.example.posebook.manager.FirebaseManager
import com.example.posebook.manager.Review
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

enum class RatingDesc (val desc: String) {
    ONESTAR("Not a good photo spot!"),
    TWOSTAR("Mediocre photo spot!"),
    THREESTAR("Good photo spot!"),
    FOURSTAR("Superb photo spot!"),
    FIVESTAR("Best photo spot ever!")
}
class SubmitReviewPopupFragment(var locationTitle: String, var locationSubTitle: String, var coord: LatLng) :
    BottomSheetDialogFragment() {
    var text = ""
    companion object {
        const val tag = "SubmitReviewPopupFragment"
    }

    private var reviewRating = 0

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialog
    }

    private fun replaceDoubleToString(coord: Double): String {
        return coord.toString().replace(".", "_")
    }

    // TODO: Temp Placeholder: Web Call to firebase will be added later.
    private fun submitReview() {
        dismiss()
        FirebaseManager.writeReview(
            "${replaceDoubleToString(coord.latitude)}${replaceDoubleToString(coord.longitude)}",
            Review(
                CityLocation(
                    locationTitle,
                    locationSubTitle
                ),
                text,
                reviewRating
            )

        )
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
            text = view.findViewById<TextInputEditText>(R.id.reviewDesc).text.toString()
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
                    reviewRating = intRating
                }
            }
        })
        return view
    }
}