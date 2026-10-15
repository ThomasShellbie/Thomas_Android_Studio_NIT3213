package com.s8113459.assignment2.ui.common

/**
 * Turns an API property name into a readable label:
 * "artistName" -> "Artist Name", "date_of_birth" -> "Date Of Birth", "property1" -> "Property 1".
 */
fun humanizeKey(key: String): String =
    key.replace('_', ' ')
        .replace('-', ' ')
        .replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .replace(Regex("([A-Za-z])([0-9])"), "$1 $2")
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercaseChar() } }
