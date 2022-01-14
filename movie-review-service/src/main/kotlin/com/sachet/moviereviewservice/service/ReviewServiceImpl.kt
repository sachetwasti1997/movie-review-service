package com.sachet.moviereviewservice.service

import com.sachet.moviereviewservice.model.Review
import com.sachet.moviereviewservice.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewServiceImpl
    @Autowired
    constructor(
        val reviewRepository: ReviewRepository
    )
    : ReviewService {

    override fun addReview(review: Review): Mono<Review> = reviewRepository.save(review)

    override fun saveAllReviews(reviews: List<Review>): Flux<Review> = reviewRepository.saveAll(reviews)

    override fun deleteAllReviews(): Mono<Void> = reviewRepository.deleteAll()

    override fun getAllReviews(): Flux<Review> = reviewRepository.findAll()
}















