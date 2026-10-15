package com.s8113459.assignment2.domain.model

/** Content of the dashboard for one keypass (topic). */
data class Dashboard(
    val entities: List<Entity>,
    val entityTotal: Int,
)
