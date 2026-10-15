package com.s8113459.assignment2.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.domain.model.EntityField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Exposes the entity selected on the dashboard (passed as a Parcelable argument). */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val entity: Entity = requireNotNull(savedStateHandle.get<Entity>(KEY_ENTITY)) {
        "DetailsViewModel requires an '$KEY_ENTITY' argument"
    }

    val title: String get() = entity.title.ifBlank { entity.fields.firstOrNull()?.key.orEmpty() }

    /** Every property of the entity (title included), shown as label/value rows. */
    val fields: List<EntityField> get() = entity.fields

    val description: String? get() = entity.description

    companion object {
        const val KEY_ENTITY = "entity"
    }
}
