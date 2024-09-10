package edu.bluejack23_1.petia.model

data class Comment(
    val shelterID: String? = null,
    val userID: String? = null,
    val comment: String? = null,
    var documentID: String ?= null
) {
    constructor() : this("", "", "", "")
}