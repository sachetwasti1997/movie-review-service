package com.sachet.moviereviewservice.unit

import com.sachet.moviereviewservice.REVIEW_BASE_URL
import com.sachet.moviereviewservice.errorHandler.GlobalErrorHandler
import com.sachet.moviereviewservice.handler.ReviewHandler
import com.sachet.moviereviewservice.model.Review
import com.sachet.moviereviewservice.repository.ReviewRepository
import com.sachet.moviereviewservice.router.ReviewRouter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.util.*

@WebFluxTest
@ContextConfiguration(classes = [ReviewRouter::class, ReviewHandler::class, GlobalErrorHandler::class])
@AutoConfigureWebTestClient
class ReviewsUnitTesting
@Autowired
constructor(val webTestClient: WebTestClient)
{

    @MockBean
    val reviewRepository:ReviewRepository ?= null

    val id = UUID.randomUUID().toString()

    @Test
    fun saveReview(){
        val review = Review(
            movieInfoId = "abcd",
            comment = "Awesome Movie",
            rating = 9.8
        )

        Mockito.`when`(reviewRepository?.save(review)).thenReturn(
            Mono.just(Review(
                    reviewId = id,
                    movieInfoId = "abcd",
                    comment = "Awesome Movie",
                    rating = 9.8
                )
            )
        )

        webTestClient
            .post()
            .uri {
                it.path(REVIEW_BASE_URL).build()
            }
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<Review>()
            .consumeWith {
                val res = it.responseBody
                val status = it.status
                Assertions.assertNotNull(res?.reviewId)
                Assertions.assertEquals(HttpStatus.CREATED, status)
            }
    }

    @Test
    fun saveReview_validation(){
        val review = Review(
            comment = "Awesome Movie",
            rating = -9.8
        )

        Mockito.`when`(reviewRepository?.save(review)).thenReturn(
            Mono.just(Review(
                reviewId = id,
                movieInfoId = "abcd",
                comment = "Awesome Movie",
                rating = 9.8
            )
            )
        )

        webTestClient
            .post()
            .uri {
                it.path(REVIEW_BASE_URL).build()
            }
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody<String>()

    }

}