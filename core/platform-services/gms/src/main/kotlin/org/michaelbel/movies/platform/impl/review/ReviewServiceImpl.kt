package org.michaelbel.movies.platform.impl.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import javax.inject.Inject
import org.michaelbel.movies.platform.review.ReviewService

class ReviewServiceImpl @Inject constructor(
    private val reviewManager: ReviewManager
): ReviewService {

    override fun requestReview(activity: Activity) {
        reviewManager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewManager.launchReviewFlow(activity, task.result)
            }
        }
    }
}