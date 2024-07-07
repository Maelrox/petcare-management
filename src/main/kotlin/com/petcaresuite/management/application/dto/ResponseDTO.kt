package com.petcaresuite.management.application.dto

data class ResponseDTO(
    val success: Boolean?,
    val message: String?,
) {
    companion object {
        fun generateSuccessResponse(isSuccess: Boolean, message: String): ResponseDTO {
            return ResponseDTO(
                success = isSuccess,
                message = message
            )
        }
    }
}