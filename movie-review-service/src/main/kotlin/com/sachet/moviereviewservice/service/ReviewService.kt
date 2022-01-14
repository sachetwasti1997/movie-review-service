package com.sachet.moviereviewservice.service

import com.sachet.moviereviewservice.model.Review
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReviewService {
    fun addReview(review: Review):Mono<Review>
    fun saveAllReviews(reviews: List<Review>):Flux<Review>
    fun deleteAllReviews():Mono<Void>
    fun getAllReviews(): Flux<Review>
}