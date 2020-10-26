package com.example.model

data class User(
    val id: String,
    val email: String,
    val hashedPass: ByteArray,
    val count: Int,
    val permissionLevel: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (!hashedPass.contentEquals(other.hashedPass)) return false
        if (count != other.count) return false
        if (permissionLevel != other.permissionLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + hashedPass.contentHashCode()
        result = 31 * result + count
        result = 31 * result + permissionLevel.hashCode()
        return result
    }
}

// logged in user exists so that hashedPass field does not have to be nullable
data class LoggedInUser(
    val id: String,
    val permissionLevel: String
)