package com.s8113459.assignment2.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `camelCase keys become title case words`() {
        assertEquals("Artist Name", humanizeKey("artistName"))
        assertEquals("Year Created", humanizeKey("yearCreated"))
    }

    @Test
    fun `snake and kebab case keys are split`() {
        assertEquals("Date Of Birth", humanizeKey("date_of_birth"))
        assertEquals("Orbit Period", humanizeKey("orbit-period"))
    }

    @Test
    fun `digits are separated from letters`() {
        assertEquals("Property 1", humanizeKey("property1"))
    }

    @Test
    fun `simple and upper case keys are kept readable`() {
        assertEquals("Name", humanizeKey("name"))
        assertEquals("URL", humanizeKey("URL"))
    }
}
