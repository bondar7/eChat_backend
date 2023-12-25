package com.echat_backend.data.data_sources

import com.echat_backend.data.models.User

interface UserDataSource {
    suspend fun insertUser(user: User): Boolean
    suspend fun getUserByUsername(username: String): User?
    suspend fun checkUsername(username: String): Boolean
    suspend fun changeUsername(usernameToFindUser: String, newUsername: String)
    suspend fun checkPassword(usernameToFindUser: String, password: String): Boolean
    suspend fun changePassword(usernameToFindUser: String, newPassword: String): Boolean
    suspend fun changeEmail(usernameToFindUser: String, newEmail: String): Boolean
    suspend fun changeBio(usernameToFindUser: String, newBio: String): Boolean
}