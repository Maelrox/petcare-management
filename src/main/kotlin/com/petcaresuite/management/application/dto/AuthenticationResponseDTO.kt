package com.petcaresuite.management.application.dto

data class AuthenticationResponseDTO(
    var token: String? = null
) {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        private var token: String? = null

        fun token(token: String?): Builder {
            this.token = token
            return this
        }

        fun build(): AuthenticationResponseDTO {
            return AuthenticationResponseDTO(token)
        }
    }
}