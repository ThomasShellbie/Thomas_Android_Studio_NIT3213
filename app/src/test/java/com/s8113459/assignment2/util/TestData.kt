package com.s8113459.assignment2.util

import com.s8113459.assignment2.domain.model.Dashboard
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.domain.model.EntityField

object TestData {
    val starryNight = Entity(
        fields = listOf(
            EntityField("artistName", "Vincent van Gogh"),
            EntityField("title", "The Starry Night"),
            EntityField("year", "1889"),
        ),
        description = "Painted in June 1889, it depicts the view from his asylum room.",
    )

    val monaLisa = Entity(
        fields = listOf(
            EntityField("artistName", "Leonardo da Vinci"),
            EntityField("title", "Mona Lisa"),
            EntityField("year", "1503"),
        ),
        description = "A half-length portrait painting.",
    )

    val dashboard = Dashboard(entities = listOf(starryNight, monaLisa), entityTotal = 2)
}
