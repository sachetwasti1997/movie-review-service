package com.sachet.moviereviewservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Document(collection = "reviews")
data class Review(
    @Id
    val reviewId: String ?= null,
    @field: NotNull(message = "Movie Id cannot be null")
    var movieInfoId: String ?= null,
    var comment: String ?= null,
    @field: Min(value = 0L, message = "Rating cannot be negative!")
    var rating: Double ?= null
)