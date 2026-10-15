package com.s8113459.assignment2.data

import com.google.gson.JsonParser
import com.s8113459.assignment2.data.mapper.EntityMapper
import com.s8113459.assignment2.data.remote.dto.DashboardResponse
import com.s8113459.assignment2.domain.model.EntityField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EntityMapperTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    @Test
    fun `description is separated from the other fields and order is kept`() {
        val entity = EntityMapper.toEntity(
            obj("""{"artistName":"Vincent van Gogh","year":1889,"description":"Famous painting"}"""),
        )

        assertEquals(
            listOf(EntityField("artistName", "Vincent van Gogh"), EntityField("year", "1889")),
            entity.fields,
        )
        assertEquals("Famous painting", entity.description)
        assertEquals("Vincent van Gogh", entity.title)
    }

    @Test
    fun `numbers are not turned into decimals`() {
        val entity = EntityMapper.toEntity(obj("""{"population":8000000,"ratio":1.5}"""))

        assertEquals("8000000", entity.fields[0].value)
        assertEquals("1.5", entity.fields[1].value)
    }

    @Test
    fun `arrays are joined and nulls become empty`() {
        val entity = EntityMapper.toEntity(obj("""{"tags":["a","b"],"note":null}"""))

        assertEquals("a, b", entity.fields[0].value)
        assertEquals("", entity.fields[1].value)
    }

    @Test
    fun `missing or blank description becomes null`() {
        assertNull(EntityMapper.toEntity(obj("""{"name":"Pluto"}""")).description)
        assertNull(EntityMapper.toEntity(obj("""{"name":"Pluto","description":"  "}""")).description)
    }

    @Test
    fun `description key is matched case-insensitively`() {
        val entity = EntityMapper.toEntity(obj("""{"name":"Pluto","Description":"Dwarf planet"}"""))

        assertEquals("Dwarf planet", entity.description)
        assertEquals(1, entity.fields.size)
    }

    @Test
    fun `dashboard total falls back to the list size when missing`() {
        val dashboard = EntityMapper.toDashboard(
            DashboardResponse(entities = listOf(obj("""{"name":"A"}"""), obj("""{"name":"B"}""")), entityTotal = null),
        )
        assertEquals(2, dashboard.entityTotal)
    }

    @Test
    fun `null entity list becomes an empty dashboard`() {
        val dashboard = EntityMapper.toDashboard(DashboardResponse(entities = null, entityTotal = null))

        assertTrue(dashboard.entities.isEmpty())
        assertEquals(0, dashboard.entityTotal)
    }
}
