package com.sachet.moviereviewservice.handler

import com.sachet.moviereviewservice.exception.ReviewDataException
import com.sachet.moviereviewservice.exception.ReviewNotFoundException
import com.sachet.moviereviewservice.model.Review
import com.sachet.moviereviewservice.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.validation.Validator

@Component
class ReviewHandler
@Autowired
constructor(
    val reviewRepository: ReviewRepository,
    val validator: Validator
    ) {

    fun addReview(request: ServerRequest):Mono<ServerResponse> {
        return request.bodyToMono(Review::class.java)
            .doOnNext {
                validateRequsest(it)
            }
            .flatMap {
                reviewRepository.save(it)
            }
            .flatMap {
                ServerResponse.status(HttpStatus.CREATED).bodyValue(it)
            }
    }

    private fun validateRequsest(review: Review?) {
        val constraintVoilations = validator.validate(review)
        if (constraintVoilations.size > 0) {
            val consStr = constraintVoilations
                .stream()
                .map {
                    it.message
                }
                .collect(Collectors.joining(","))
            throw ReviewDataException(consStr)
        }
    }

    fun getAllReviews():Mono<ServerResponse> {
        val allReviews = reviewRepository.findAll()
        return ServerResponse.ok().body(allReviews, Review::class.java)
    }

    fun updateReview(request: ServerRequest): Mono<ServerResponse> {

        val reviewId = request.pathVariable("id")

        val existingReview = reviewRepository
            .findById(reviewId)
            .switchIfEmpty(Mono.error(ReviewNotFoundException("No review found")))

        return existingReview
            .flatMap {
                    review ->
                        request.bodyToMono(Review::class.java)
                            .map {
                                review.comment = it.comment
                                review.rating = it.rating
                                return@map review
                            }
                            .flatMap {
                                reviewRepository.save(it)
                            }
                            .flatMap {
                                ServerResponse.ok().bodyValue(it)
                            }
                        }

    }

    fun deleteReview(request: ServerRequest):Mono<ServerResponse> {

        val reviewId = request.pathVariable("id")

        val existingReview = reviewRepository.findById(reviewId)

        return existingReview
                .flatMap {
                    reviewRepository.delete(it)
                }
                .then(ServerResponse.noContent().build())

    }

    fun getByMovieInfoId(request: ServerRequest):Mono<ServerResponse> {
        val movieInfoId = request.pathVariable("movieInfoId")
        val movieReviewsFlux = reviewRepository
            .findByMovieInfoId(movieInfoId)
            .switchIfEmpty(Mono.error(ReviewNotFoundException("No Reviews found for the given movie")))
        return ServerResponse.ok().body(movieReviewsFlux, Review::class.java)
    }

}















