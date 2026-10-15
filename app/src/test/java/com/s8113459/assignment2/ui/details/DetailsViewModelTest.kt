package com.s8113459.assignment2.ui.details

import androidx.lifecycle.SavedStateHandle
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.domain.model.EntityField
import com.s8113459.assignment2.util.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailsViewModelTest {

    private fun createViewModel(entity: Entity?) = DetailsViewModel(
        SavedStateHandle(if (entity != null) mapOf(DetailsViewModel.KEY_ENTITY to entity) else emptyMap()),
    )

    @Test
    fun `exposes all fields and the description of the selected entity`() {
        val viewModel = createViewModel(TestData.starryNight)

        assertEquals("Vincent van Gogh", viewModel.title)
        assertEquals(TestData.starryNight.fields, viewModel.fields)
        assertEquals(TestData.starryNight.description, viewModel.description)
    }

    @Test
    fun `entity without description exposes null description`() {
        val viewModel = createViewModel(Entity(listOf(EntityField("name", "Pluto")), description = null))

        assertNull(viewModel.description)
    }

    @Test
    fun `falls back to the first key when the first value is blank`() {
        val viewModel = createViewModel(Entity(listOf(EntityField("name", "")), description = null))

        assertEquals("name", viewModel.title)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `missing entity argument fails fast`() {
        createViewModel(null)
    }
}
