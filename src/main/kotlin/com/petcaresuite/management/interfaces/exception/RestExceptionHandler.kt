package com.petcaresuite.management.interfaces.exception

import com.petcaresuite.management.application.dto.ErrorResponseDTO
import com.petcaresuite.management.application.service.messages.InternalErrors
import com.petcaresuite.management.application.service.messages.Responses
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
            message = ex.message ?: Responses.TOKEN_EXPIRED,
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
            message = ex.message ?: Responses.USER_NAME_OR_PASSWORD_INVALID,
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessException(ex: IllegalAccessException): ResponseEntity<ErrorResponseDTO> {
        logger.warn(InternalErrors.ILLEGAL_ACCESS.format(ex.message))
        val errorResponse = ErrorResponseDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.FORBIDDEN.value(),
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = ex.message ?: Responses.OPERATION_NOT_ALLOWED,
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
            message = ex.message ?: Responses.MALFORMED_JSON,
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<ErrorResponseDTO> {
        logger.error(InternalErrors.UNHANDLED_EXCEPTION.format(ex.message))
        val errorResponseDTO = ErrorResponseDTO(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = ex.message ?: InternalErrors.UNHANDLED_EXCEPTION,
            path = getRequestPath()
        )
        return ResponseEntity.status(errorResponseDTO.status).body(errorResponseDTO)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponseDTO> {
        logger.error(InternalErrors.UNHANDLED_EXCEPTION.format(ex.message))
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
                        Responses.REQUIRED_FIELD_MISSING
                    cause.message?.contains("unique", ignoreCase = true) == true ->
                        Responses.DUPLICATE_ENTRY
                    else -> Responses.PERSISTENCE_ERROR
                }
            }
            else -> Responses.GENERIC_ERROR
        }
    }

    private fun getRequestPath(): String {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        return requestAttributes?.request?.requestURI ?: "/undefined"
    }

}