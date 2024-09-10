package edu.bluejack23_1.petia.model

import com.google.firebase.Timestamp

data class Messenger(
    val shelterID: String? = null,
    val userID: String? = null,
    val message: List<Message>? = null,
    val latestMessage: String? = null,
    val lastUpdated: Timestamp? = null,
    var documentID: String ?= null
)