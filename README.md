# eChat Backend 🛡️

**eChat Backend** is the server-side application for the eChat Android app, built using **Ktor**. It provides real-time WebSocket communication, user authentication, and database management with **MongoDB (KMongo)**.

&nbsp;

## ✨ Features

- 💬 **Real-time Chat** (WebSockets)
- 🔐 **JWT Authentication & Sessions**
- 📚 **MongoDB Database (KMongo)**
- 🔍 **Logging & Monitoring** (Logback, Call Logging)
- 🛠️ **Dependency Injection** (Koin)
- 🌐 **REST API** (Ktor with Content Negotiation)

&nbsp;

## 🔧 Tech Stack

- **Kotlin 1.9.21**
- **Ktor 2.3.7**
- **MongoDB (KMongo 4.5.1)**
- **JWT Authentication**
- **Koin 3.1.2 (Dependency Injection)**
- **Commons Codec 1.15**
- **Logback 1.4.11**
- **OkHttp 4.9.3**

&nbsp;

## 🛠️ Installation

### Prerequisites
- JDK **17+**
- MongoDB **running locally or in the cloud**
- [eChat Frontend](https://github.com/bondar7/eChat) 

### Clone Repository
```sh
git clone https://github.com/your-username/eChat-backend.git
cd eChat-backend
```

&nbsp;

## 📂 Dependencies

```kotlin
// Ktor Core
implementation("io.ktor:ktor-server-core-jvm")
implementation("io.ktor:ktor-server-netty-jvm")
implementation("io.ktor:ktor-server-websockets-jvm")
implementation("io.ktor:ktor-server-auth-jwt-jvm")
implementation("io.ktor:ktor-server-sessions-jvm")
implementation("io.ktor:ktor-server-call-logging-jvm")

// MongoDB (KMongo)
implementation("org.litote.kmongo:kmongo:4.5.1")
implementation("org.litote.kmongo:kmongo-coroutine:4.5.1")

// Dependency Injection (Koin)
implementation("io.insert-koin:koin-ktor:3.1.2")
implementation("io.insert-koin:koin-logger-slf4j:3.1.2")

// Logging
implementation("ch.qos.logback:logback-classic:1.4.11")
```

*(Full dependencies available in `build.gradle.kts`)*

&nbsp;

# API Endpoints

## Authentication
- **Sign Up**: `POST /signup`
- **Log In**: `POST /login`
- **Authenticate**: `GET /authenticate` (Requires JWT)

## User Management
- **Change Avatar**: `POST /change-avatar`
- **Check Username**: `POST /check-username`
- **Change Username**: `POST /change-username`
- **Change Name**: `POST /change-name`
- **Check Password**: `POST /check-password`
- **Change Email**: `POST /change-email`
- **Check Email**: `POST /check-email`
- **Change Password**: `POST /change-password`
- **Change Bio**: `POST /change-user-bio`
- **Get Users by Username**: `GET /get-users-by-username`

## Messaging
- **Chat WebSocket**: `WS /chat/{sessionId}/{currentUserId}`
- **Get Messages by Session ID**: `GET /get-messages-by-sessionID`

## Sessions
- **Create Chat Session**: `POST /create-session`
- **Get Sessions by User ID**: `GET /get-sessions-by-userID`

## Secure Routes
- **Get Secret Info**: `GET /secret` (Requires JWT authentication)

&nbsp;

## 🛠️ Environment Variables

Set up a `.env` file or environment variables for:

```plaintext
MONGO_URI=mongodb://localhost:27017/echat
JWT_SECRET=your_jwt_secret_key
```

&nbsp;

## 🙏 Contributing

Contributions, issues, and feature requests are welcome! Feel free to **fork** and submit pull requests.

&nbsp;

## 📜 License

MIT License © [Maksym Bondar](https://github.com/bondar7)

&nbsp;

