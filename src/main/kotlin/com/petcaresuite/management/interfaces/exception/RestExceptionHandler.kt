package com.petcaresuite.management.interfaces.exception

import com.petcaresuite.management.application.dto.ErrorResponseDTO
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime

@ControllerAdvice
class RestExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: ExpiredJwtException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message ?: "Token Expired",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message ?: "Username/Password Invalid",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessException(ex: IllegalAccessException): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Illegal Access " + ex.message ?: "ex.stackTraceToString()")
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.FORBIDDEN.value(),
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = ex.message ?: "Operation Not Allowed",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDTO> {
        val errors = ex.bindingResult.fieldErrors.map { error ->
            "${error.field}: ${error.defaultMessage}"
        }
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = errors.joinToString(", "),
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class, IllegalArgumentException::class])
    fun handleHttpMessageNotReadableException(ex: Exception): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Malformed JSON request",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<ErrorResponseDTO> {
        logger.error("Unhandled exception " + ex.message ?: "ex.stackTraceToString()")
        val errorResponseDTO = ErrorResponseDTO(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.message ?: "Undefined error",
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponseDTO.status).body(errorResponseDTO)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponseDTO> {
        logger.error("Unhandled exception " + ex.message ?: "ex.stackTraceToString()")
        val errorMessage = generateUserFriendlyErrorMessage(ex)
        val errorResponseDTO = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = errorMessage,
            path = getRequestPath()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO)
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