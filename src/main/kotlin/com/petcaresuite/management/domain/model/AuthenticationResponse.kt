package com.petcaresuite.management.domain.model

data class AuthenticationResponse(
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

        fun build(): AuthenticationResponse {
            return AuthenticationResponse(token)
        }
    }
}