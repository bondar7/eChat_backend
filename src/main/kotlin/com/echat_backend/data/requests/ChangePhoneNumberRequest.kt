package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangePhoneNumberRequest(
    val usernameToFindUser: String,
    val newPhoneNumber: String
)
