package com.echat_backend

import com.echat_backend.authenticate
import com.echat_backend.data.data_sources.UserDataSource
import com.echat_backend.data.models.User
import com.echat_backend.data.requests.*
import com.echat_backend.data.responses.AuthResponse
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.hashing.SHA256HashingService
import com.echat_backend.security.hashing.SaltedHash
import com.echat_backend.security.token.JwtTokenService
import com.echat_backend.security.token.TokenClaim
import com.echat_backend.security.token.TokenConfig
import com.echat_backend.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post(path = "signup") {
        val request = call.receiveOrNull<SignUpRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        val isUserAlreadyExists = userDataSource.getUserByUsername(request.username) != null

        if (areFieldsBlank) {
            call.respond(HttpStatusCode.Conflict, "Fill up all fields!")
            return@post
        }
        if (isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, "Password too short (less than 8 characters)")
            return@post
        }
        if (isUserAlreadyExists) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            phoneNumber = request.phoneNumber,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.logIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post(path = "login") {
        val request = call.receiveOrNull<LogInRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val foundUser = userDataSource.getUserByUsername(request.username)

        if (foundUser == null) {
            call.respond(HttpStatusCode.BadRequest, "Incorrect username or password")
            return@post
        }

        val isPwCorrect = hashingService.verify(
            request.password,
            SaltedHash(
                hash = foundUser.password,
                salt = foundUser.salt
            )
        )

        if (!isPwCorrect) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(
                name = "userId",
                value = foundUser.id.toString()
            )
        )

        call.respond(
            HttpStatusCode.OK,
            AuthResponse(
                username = foundUser.username,
                phoneNumber = foundUser.phoneNumber,
                bio = foundUser.userBio,
                token = token
            )
        )
    }
}

fun Route.changeUsername(
    userDataSource: UserDataSource
) {
    post("change-username") {
        val request = call.receiveOrNull<ChangeUsernameRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changeUsername(request.usernameToFindUser, request.newUsername)
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.changePhoneNumber(
    userDataSource: UserDataSource
) {
    post("change-phoneNumber") {
        val request = call.receiveOrNull<ChangePhoneNumberRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changePhoneNumber(request.usernameToFindUser, request.newPhoneNumber)
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.changePassword(
    userDataSource: UserDataSource
) {
    post("change-password") {
        val request = call.receiveOrNull<ChangePasswordRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changePassword(request.usernameToFindUser, request.newPassword)
        call.respond(HttpStatusCode.OK)
    }
}


fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}