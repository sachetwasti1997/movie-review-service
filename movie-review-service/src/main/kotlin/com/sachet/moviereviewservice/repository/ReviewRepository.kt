package com.sachet.moviereviewservice.repository

import com.sachet.moviereviewservice.model.Review
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ReviewRepository: ReactiveMongoRepository<Review, String> {
    fun findByMovieInfoId(movieInfoId:String):Flux<Review>
}