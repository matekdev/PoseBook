package com.example.posebook.manager

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.subjects.PublishSubject

data class ReviewData (var rating: Long, var review: String) {
    constructor() : this(0, "")
}

class FirebaseManager {
    companion object {
        private val database = Firebase.database
        var reviewSubmittedStatus = PublishSubject.create<Boolean>()

        fun writeReview(location: String, cityLocation: CityLocation, reviewData: ReviewData) {
            val value = hashMapOf(
                "cityLocation" to cityLocation,
                "reviewData" to reviewData
            );
            database.getReference(location).setValue(value) { error, ref ->
                    if (error == null) {
                        reviewSubmittedStatus.onNext(true)
                    } else {
                        reviewSubmittedStatus.onNext(false)
                    }
                }
        }
    }
}