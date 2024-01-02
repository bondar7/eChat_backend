package com.echat_backend.routes

import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.data.models.User
import com.echat_backend.data.requests.*
import com.echat_backend.data.responses.AuthResponse
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.hashing.SaltedHash
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
            email = request.email,
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
                id = foundUser.id,
                username = foundUser.username,
                name = foundUser.name,
                email = foundUser.email,
                bio = foundUser.userBio,
                avatar = foundUser.avatar,
                token = token
            )
        )
    }
}

fun Route.changeAvatar(
    userDataSource: UserDataSource
) {
    post("change-avatar") {
        val request = call.receiveOrNull<ChangeAvatarRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changeAvatar(
            usernameToFindUser = request.usernameToFindUser,
            avatar = request.avatar
        )
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.checkUsername(
    userDataSource: UserDataSource
) {
    post("check-username") {
        val request = call.receiveOrNull<String>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val response = userDataSource.checkUsername(request)
        call.respond(HttpStatusCode.OK, response)
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

fun Route.changeName(
    userDataSource: UserDataSource
) {
    post("change-name") {
        val request = call.receiveOrNull<ChangeNameRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changeName(request.usernameToFindUser, request.newName)
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.checkPassword(
    userDataSource: UserDataSource
) {
    post("check-password") {
        val request = call.receiveOrNull<CheckPasswordRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val response = userDataSource.checkPassword(request.usernameToFindUser, request.password)
        call.respond(HttpStatusCode.OK, response)
    }
}


fun Route.changeEmail(
    userDataSource: UserDataSource
) {
    post("change-email") {
        val request = call.receiveOrNull<ChangeEmailRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changeEmail(request.usernameToFindUser, request.newEmail)
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.checkEmail(
    userDataSource: UserDataSource
) {
    post("check-email") {
        val request = call.receiveOrNull<String>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val response = userDataSource.checkEmail(request)
        call.respond(HttpStatusCode.OK, response)
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
fun Route.changeUserBio(
    userDataSource: UserDataSource
) {
    post("change-user-bio") {
        val request = call.receiveOrNull<ChangeUserBioRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        userDataSource.changeBio(request.usernameToFindUser, request.newBio)
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


fun Route.getUsersByUsername(
    userDataSource: UserDataSource
) {
    get("get-users-by-username") {
        val request = call.parameters["username"] ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val response = userDataSource.getUsersByUsername(request)
        call.respond(HttpStatusCode.OK, response)
    }
}