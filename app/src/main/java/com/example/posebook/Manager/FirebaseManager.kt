package com.example.posebook.manager

import android.text.Editable
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.subjects.PublishSubject

data class Review(var cityLocation: CityLocation, var review: String) {
    constructor() : this(CityLocation(), "")
}

class FirebaseManager {
    companion object {
        private val database = Firebase.database
        var reviewSubmittedStatus = PublishSubject.create<Boolean>()

        fun writeReview(location: String, review: Review) {
            database.getReference(location).child("reviews")
                .setValue(review, DatabaseReference.CompletionListener {error, ref ->
                    if (error == null) {
                        reviewSubmittedStatus.onNext(true)
                    } else {
                        reviewSubmittedStatus.onNext(false)
                    }
                })
        }

    }
}