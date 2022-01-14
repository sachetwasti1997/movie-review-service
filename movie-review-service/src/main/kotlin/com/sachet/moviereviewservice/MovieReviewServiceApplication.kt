package com.sachet.moviereviewservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MovieReviewServiceApplication

fun main(args: Array<String>) {
	runApplication<MovieReviewServiceApplication>(*args)
}
