package com.s8113459.assignment2.data.remote.dto

import com.google.gson.JsonObject

/**
 * Response of GET /dashboard/{keypass}.
 * Entities are kept as raw [JsonObject]s because their property names differ for every topic;
 * [com.s8113459.assignment2.data.mapper.EntityMapper] turns them into domain models.
 */
data class DashboardResponse(
    val entities: List<JsonObject>?,
    val entityTotal: Int?,
)
