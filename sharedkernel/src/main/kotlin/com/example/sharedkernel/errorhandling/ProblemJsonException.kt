package com.example.sharedkernel.errorhandling

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class ProblemJsonException(
    val type: String,
    val title: String,
    val detail: String,
    val extensions: Map<String, JsonElement> = emptyMap(),
    val statusCode: Int,
) : RuntimeException()