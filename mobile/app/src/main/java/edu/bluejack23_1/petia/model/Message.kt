package edu.bluejack23_1.petia.model

import com.google.firebase.Timestamp

data class Message (
    val createdAt: Timestamp,
    val from: String,
    val text: String,
    val to: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "createdAt" to createdAt,
            "from" to from,
            "text" to text,
            "to" to to
        )
    }
}