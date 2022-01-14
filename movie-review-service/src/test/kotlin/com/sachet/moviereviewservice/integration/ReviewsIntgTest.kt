package com.sachet.moviereviewservice.integration

import com.sachet.moviereviewservice.REVIEW_BASE_URL
import com.sachet.moviereviewservice.model.Review
import com.sachet.moviereviewservice.service.ReviewService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReviewsIntgTest
@Autowired
constructor(
    val reviewService: ReviewService,
    val webTestClient: WebTestClient
)
{
    val id = UUID.randomUUID().toString()

    @BeforeEach
    internal fun setUp() {
        val reviewList = listOf<Review>(
            Review(
                movieInfoId = "abcd",
                comment = "Awesome Movie",
                rating = 9.8
            ),
            Review(
                movieInfoId = "abcd123",
                comment = "It was good Movie",
                rating = 9.0
            ),
            Review(
                reviewId = id,
                movieInfoId = id,
                comment = "Good Movie",
                rating = 8.0
            )
        )
        reviewService.saveAllReviews(reviewList).blockLast()
    }

    @AfterEach
    internal fun tearDown() {
        reviewService.deleteAllReviews().block()
    }

    @Test
    fun createNewReview(){
        val newReview = Review(
            movieInfoId = id,
            comment = "Loved the actors! Wonderful Movie!",
            rating = 9.9
        )

        webTestClient
            .post()
            .uri {
                it.path(REVIEW_BASE_URL).build()
            }
            .bodyValue(newReview)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody<Review>()
            .consumeWith {
                val body = it.responseBody
                val status = it.status
                Assertions.assertNotNull(body?.reviewId)
                Assertions.assertEquals(HttpStatus.CREATED, status)
            }
    }

    @Test
    fun createNewReview_validation() {
        val newReview = Review(
            movieInfoId = id,
            comment = "Loved the actors! Wonderful Movie!",
            rating = -9.9
        )

        webTestClient
            .post()
            .uri {
                it.path(REVIEW_BASE_URL).build()
            }
            .bodyValue(newReview)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun getAllReviews(){
        webTestClient
            .get()
            .uri {
                it.path(REVIEW_BASE_URL).build()
            }
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(Review::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Review>> {
                val body = it.responseBody
                println(body)
                Assertions.assertEquals(3, body?.size)
            }
    }

    @Test
    fun updateReview(){
        val comment = "One of the best!"
        val rating = 9.0
        val review = Review(
            movieInfoId = id,
            comment = comment,
            rating = rating
        )

        webTestClient
            .put()
            .uri {
                it.path("$REVIEW_BASE_URL/$id").build()
            }
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody<Review>()
            .consumeWith {
                val res = it.responseBody
                val status = it.status
//                println(res)
                Assertions.assertEquals(HttpStatus.OK, status)
                Assertions.assertEquals(comment, res?.comment)
                Assertions.assertEquals(rating, res?.rating)
            }
    }

    @Test
    fun deleteReview(){
        webTestClient
            .delete()
            .uri {
                it.path("$REVIEW_BASE_URL/$id").build()
            }
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody<Void>()
            .consumeWith {
                val status = it.status
                Assertions.assertEquals(HttpStatus.NO_CONTENT, status   )
            }
    }

    @Test
    fun reviewsByMovieId(){
        webTestClient
            .get()
            .uri {
                it.path("$REVIEW_BASE_URL/$id").build()
            }
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList<Review>()
            .consumeWith<WebTestClient.ListBodySpec<Review>> {
                val reviews = it.responseBody
                println(reviews)
                Assertions.assertEquals(1, reviews?.size)
            }
    }

    @Test
    fun reviewsByMovieId_validation(){
        webTestClient
            .get()
            .uri {
                it.path("$REVIEW_BASE_URL/abc").build()
            }
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody<String>()
    }

    @Test
    fun updateReview_validation(){
        val comment = "One of the best!"
        val rating = 9.0
        val review = Review(
            movieInfoId = id,
            comment = comment,
            rating = rating
        )

        webTestClient
            .put()
            .uri {
                it.path("$REVIEW_BASE_URL/abc").build()
            }
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody<String>()
            .consumeWith {
                val res = it.responseBody
                val status = it.status
                Assertions.assertEquals(HttpStatus.NOT_FOUND, status)
            }
    }

}


















