package com.sachet.moviereviewservice.router

import com.sachet.moviereviewservice.REVIEW_BASE_URL
import com.sachet.moviereviewservice.handler.ReviewHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class ReviewRouter {

    @Bean
    fun reviewsRoute(reviewHandler: ReviewHandler):RouterFunction<ServerResponse>{

        return route()
            .nest(path(REVIEW_BASE_URL)){
                builder: RouterFunctions.Builder -> builder
                .POST {
                    reviewHandler.addReview(it)
                }
                .GET("/{movieInfoId}"){
                    reviewHandler.getByMovieInfoId(it)
                }
                .GET(""){
                    reviewHandler.getAllReviews()
                }
                .PUT("/{id}") {
                    reviewHandler.updateReview(it)
                }
                .DELETE("/{id}"){
                    reviewHandler.deleteReview(it)
                }
            }
            .GET("/v1/hello") {
                ServerResponse.ok().bodyValue("Hello")
            }
            .build()

    }



}

