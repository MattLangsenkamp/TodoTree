package com.example.model

import com.auth0.jwt.interfaces.DecodedJWT




data class EncodedTokens(
        val AccessToken: String?,
        val RefreshToken: String?)

data class DecodedTokens(
        val AccessToken: DecodedJWT?,
        val RefreshToken: DecodedJWT?)