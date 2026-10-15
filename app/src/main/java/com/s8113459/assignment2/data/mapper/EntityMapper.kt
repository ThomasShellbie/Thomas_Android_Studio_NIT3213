package com.s8113459.assignment2.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.s8113459.assignment2.data.remote.dto.DashboardResponse
import com.s8113459.assignment2.domain.model.Dashboard
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.domain.model.EntityField

/** Converts API DTOs into domain models. Works for any topic because property names are read dynamically. */
object EntityMapper {

    const val DESCRIPTION_KEY = "description"

    fun toDashboard(response: DashboardResponse): Dashboard {
        val entities = response.entities.orEmpty().map(::toEntity)
        return Dashboard(
            entities = entities,
            entityTotal = response.entityTotal ?: entities.size,
        )
    }

    fun toEntity(json: JsonObject): Entity {
        var description: String? = null
        val fields = mutableListOf<EntityField>()

        for ((key, value) in json.entrySet()) {
            if (key.equals(DESCRIPTION_KEY, ignoreCase = true)) {
                description = valueToText(value).ifBlank { null }
            } else {
                fields += EntityField(key = key, value = valueToText(value))
            }
        }
        return Entity(fields = fields, description = description)
    }

    /** Turns any JSON value into display text: primitives as-is, arrays comma-separated, null as empty. */
    fun valueToText(element: JsonElement?): String = when {
        element == null || element.isJsonNull -> ""
        element.isJsonPrimitive -> element.asString
        element.isJsonArray -> element.asJsonArray.joinToString(", ") { valueToText(it) }
        else -> element.toString()
    }
}
