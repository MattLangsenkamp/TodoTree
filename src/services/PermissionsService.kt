package com.example.services

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.repository.ScopeRepository
import com.example.repository.UserRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject

class PermissionsService: KoinComponent {

    fun checkPermission(actionRequester: DecodedJWT, resourceOwnerId: String) {
        if (actionRequester.getClaim("permissionLevel").toString() == "Admin") return
        if (actionRequester.getClaim("key").toString() == resourceOwnerId) return

        error("User is not authorized to perform this action")
    }
}