package com.echat_backend.data.data_sources.userDT

import com.echat_backend.data.models.User
import com.echat_backend.data.responses.Person

interface UserDataSource {
    suspend fun insertUser(user: User): Boolean
    suspend fun changeAvatar(usernameToFindUser: String, avatar: ByteArray)
    suspend fun getUserByUsername(username: String): User?

    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun checkUsername(username: String): Boolean
    suspend fun changeUsername(usernameToFindUser: String, newUsername: String)
    suspend fun changeName(usernameToFindUser: String, newName: String)
    suspend fun checkPassword(usernameToFindUser: String, password: String): Boolean
    suspend fun changePassword(usernameToFindUser: String, newPassword: String): Boolean
    suspend fun checkEmail(email: String):  Boolean
    suspend fun changeEmail(usernameToFindUser: String, newEmail: String): Boolean
    suspend fun changeBio(usernameToFindUser: String, newBio: String): Boolean
    suspend fun getUsersByUsername(username: String): List<Person>
}