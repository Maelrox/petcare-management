package com.petcaresuite.management.application.service.messages

object Responses {
    const val USER_CREATED = "The user has been created"
    const val USER_UPDATED = "The user has been updated"
    const val USER_ALREADY_EXISTS = "User %s already exists"
    const val USER_UPDATE_NOT_ALLOWED =  "Only Application Admins or the user themselves can update the user"
    const val USER_NOT_VALID = "User not found"
    const val USER_PASSWORD_NOT_VALID = "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter and one special character."
    const val ROLE_NOT_FOUND = "Role %s not found"
    const val USER_AUTHENTICATED = "Authentication successfully"
    const val USER_LOGIN_TOO_MANY_ATTEMPTS = "Too many login attempts"

    const val COMPANY_CREATED = "The company has been created"
    const val COMPANY_UPDATED = "The company has been updated"
    const val COMPANY_NAME_ALREADY_EXIST = "Company with name %s already exists"
    const val COMPANY_IDENTIFICATION_ALREADY_EXIST = "Company with identification %s already exists"
    const val USER_IS_MEMBER_OF_ANOTHER_COMPANY = "You are already member of a company"
    const val USER_IS_NOT_MEMBER_OF_COMPANY = "You are not a member of this company"


}