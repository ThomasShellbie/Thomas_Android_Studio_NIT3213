package com.s8113459.assignment2.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * One key/value pair of an entity, e.g. ("artistName", "Vincent van Gogh").
 * The API returns different properties for every topic, so fields are kept generic.
 */
@Parcelize
data class EntityField(
    val key: String,
    val value: String,
) : Parcelable

/**
 * An item returned by the dashboard endpoint.
 *
 * @property fields every property except the description, in the order the API sent them.
 * @property description the long "description" text, or null if the API did not provide one.
 */
@Parcelize
data class Entity(
    val fields: List<EntityField>,
    val description: String?,
) : Parcelable {

    /** Value of the first property — used as the item's title. */
    val title: String
        get() = fields.firstOrNull()?.value.orEmpty()

    /** Properties shown under the title in the dashboard list (everything except the title). */
    val summaryFields: List<EntityField>
        get() = fields.drop(1)
}
