package com.petcaresuite.management.application.service.messages

object Responses {
    const val USER_CREATED = "The user has been created"
    const val USER_UPDATED = "The user has been updated"
    const val USER_ALREADY_EXISTS = "User %s already exists"
    const val USER_UPDATE_NOT_ALLOWED =  "Only Application Admins or the user themselves can update the user"
    const val USER_NOT_VALID = "User not found"
    const val USER_PASSWORD_NOT_VALID = "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter and one special character"
    const val ROLE_NOT_FOUND = "Role %s not found"
    const val USER_AUTHENTICATED = "Authentication successfully"
    const val USER_LOGIN_TOO_MANY_ATTEMPTS = "Too many login attempts"
    const val REGISTER_AS_SYSADMIN_ERROR = "Invalid Role"

    const val COMPANY_CREATED = "The company has been created"
    const val COMPANY_UPDATED = "The company has been updated"
    const val COMPANY_NAME_ALREADY_EXIST = "Company with name %s already exists"
    const val COMPANY_IDENTIFICATION_ALREADY_EXIST = "Company with identification %s already exists"
    const val COMPANY_IDENTIFICATION_DOESNT_EXIST = "Company with identification %s doesn't exists"
    const val COMPANY_NAME_REQUIRED = "Company name is required"
    const val COMPANY_COUNTRY_REQUIRED = "Country is required"
    const val COMPANY_IDENTIFICATION_REQUIRED = "Company identification is required"
    const val COMPANY_NAME_LENGTH_INVALID = "Company length must be between 3 and 255 characters"
    const val COMPANY_COUNTRY_LENGTH_INVALID = "Company length must be between 3 and 255 characters"
    const val COMPANY_IDENTIFICATION_LENGTH_INVALID = "Company identification length must be between 3 and 255 characters"
    const val COMPANY_WITHOUT_ROLES = "Company without roles"
    const val USER_IS_MEMBER_OF_ANOTHER_COMPANY = "You are already member of a company"
    const val USER_IS_NOT_MEMBER_OF_COMPANY = "You are not a member of this company"

    const val MALFORMED_JSON = "Malformed JSON"
    const val OPERATION_NOT_ALLOWED = "Operation Not Allowed"
    const val USER_NAME_OR_PASSWORD_INVALID = "Username/Password Invalid"
    const val TOKEN_EXPIRED = "The token has expired"

    const val REQUIRED_FIELD_MISSING = "A required field was left empty. Please fill in all required fields"
    const val DUPLICATE_ENTRY = "A duplicate entry was detected. Please ensure all fields are unique where required"
    const val PERSISTENCE_ERROR = "There was an issue with the data you provided. Please check your input and try again."
    const val GENERIC_ERROR = "An unexpected error occurred. Please try again or contact support if the problem persists."

    const val ROLE_NAME_REQUIRED = "Role name required"
    const val ROLE_LENGTH_INVALID = "Role length invalid"
    const val ROLE_COMPANY_REQUIRED = "Role company required"
    const val ROLE_ALREADY_EXIST = "Rol already exists"
    const val ROLE_CREATED = "Role created"

    const val PERMISSION_NAME_REQUIRED = "Permission name is required"
    const val PERMISSION_LENGTH_INVALID = "Permission length is invalid"
    const val PERMISSION_ROLE_REQUIRED = "Permission role required"

    const val MODULE_NAME_REQUIRED = "Module name required"
    const val MODULE_LENGTH_INVALID = "Module length is invalid"
    const val MODULE_NOT_FOUND = "Module not found"
    const val MODULE_ALREADY_EXIST = "Module already exists"
    const val MODULE_CREATED = "The module %s has been created"

    const val MODULES_ACTION_NAME_REQUIRED = "Modules action name required"
    const val MODULES_ACTION_LENGTH_INVALID = "Modules action length is invalid"
    const val MODULES_ACTION_ALREADY_EXIST = "Modules action already exists"
    const val MODULES_ACTION_CREATED = "The modules action %s has been created"

    const val PERMISSION_CREATED = "Permission %s created"
    const val PERMISSION_ALREADY_EXISTS = "Permission already exists"
    const val PERMISSION_NOT_FOUND = "Permission not found"
    const val PERMISSION_NOT_PART_OF_THE_COMPANY = "Permission is not part of the company"
    const val PERMISSION_MODULES_ACTION_CREATED = "Permission modules action created"

}