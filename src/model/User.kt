package com.example.model

data class User(val id: String,
                val email: String,
                val hashedPass: ByteArray,
                val count: Int, val permissionLevel: String)