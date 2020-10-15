package com.example.model

import java.time.Instant
import java.util.*

data class Scope(val id: String,
                 val userId: String,
                 val defaultScope: Boolean,
                 val creationTimeStamp: Instant,
                 val name: String,
                 val description: String? = null,
                 val startTime: Instant? = null,
                 val endTime: Instant? = null,
)