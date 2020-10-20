package com.example.model
import kotlinx.serialization.*
import org.litote.kmongo.*
import java.time.Instant

@Serializable
data class Todo(val id: String,
                val userId: String,
                val creationTimeStamp: Instant,
                val text: String,
                val completed: Boolean,
                val scopeId: String,
                val rootTodo: Boolean,
                val parentTodoId: String?,
                val children: List<String>,
                val childrenObjects: MutableList<Todo>? = null
)
