package com.petcaresuite.management.interfaces.exception

import com.petcaresuite.management.application.dto.ErrorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime

@ControllerAdvice
class RestExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { error ->
            "${error.field}: ${error.defaultMessage}"
        }
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = errors.joinToString(", "),
            path = getRequestPath()
        )
        logger.trace("validation trace")
        logger.debug("validation debug")
        logger.warn("validation warning")
        logger.error("validation error")
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class, IllegalArgumentException::class])
    fun handleHttpMessageNotReadableException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Malformed JSON request",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.message ?: "Undefined error",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        val errorMessage = generateUserFriendlyErrorMessage(ex)

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = errorMessage,
            path = getRequestPath()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    private fun generateUserFriendlyErrorMessage(ex: DataIntegrityViolationException): String {
        val cause = ex.cause
        return when {
            cause is ConstraintViolationException -> {
                when {
                    cause.message?.contains("not-null", ignoreCase = true) == true ->
                        "A required field was left empty. Please fill in all required fields."
                    cause.message?.contains("unique", ignoreCase = true) == true ->
                        "A duplicate entry was detected. Please ensure all fields are unique where required."
                    else -> "There was an issue with the data you provided. Please check your input and try again."
                }
            }
            else -> "An unexpected error occurred. Please try again or contact support if the problem persists."
        }
    }

    private fun getRequestPath(): String {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        return requestAttributes?.request?.requestURI ?: "/undefined"
    }

}