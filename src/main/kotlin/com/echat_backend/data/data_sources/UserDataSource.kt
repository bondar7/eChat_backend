package com.echat_backend.data.data_sources

import com.echat_backend.data.models.User

interface UserDataSource {
    suspend fun insertUser(user: User): Boolean
    suspend fun getUserByUsername(username: String): User?
    suspend fun changeUsername(usernameToFindUser: String, newUsername: String): Boolean
    suspend fun changePassword(usernameToFindUser: String, newPassword: String): Boolean
    suspend fun changePhoneNumber(usernameToFindUser: String, newPhoneNumber: String): Boolean
}