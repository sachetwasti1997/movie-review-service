package com.sachet.moviereviewservice.errorHandler

import com.sachet.moviereviewservice.exception.ReviewDataException
import com.sachet.moviereviewservice.exception.ReviewNotFoundException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalErrorHandler: ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val dataFactory = exchange.response.bufferFactory()
        val errorMessage = dataFactory.wrap(ex.message!!.toByteArray())
        if (ex is ReviewDataException){
            exchange.response.statusCode = HttpStatus.BAD_REQUEST
            return exchange.response.writeWith (Mono.just(errorMessage))
        }else if (ex is ReviewNotFoundException){
            exchange.response.statusCode = HttpStatus.NOT_FOUND
            return exchange.response.writeWith (Mono.just(errorMessage))
        }
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        return exchange.response.writeWith(Mono.just(errorMessage))
    }

}
