package com.example.model

data class User(val id: String,
                val email: String,
                val hashedPass: ByteArray,
                val count: Int,
                val permissionLevel: String)

// logged in user exists so that hashedPass field does not have to be nullable
data class LoggedInUser(val id: String,
                val permissionLevel: String)