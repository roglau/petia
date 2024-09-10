package edu.bluejack23_1.petia.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var id : String ?= null,
    var fullname: String,
    var username: String,
    var password: String,
    var email: String,
    var phone: String,
    var role: String,
    var status: Boolean,
    var profileImage: String
){
    constructor() : this("", "", "", "", "", "", "" ,false, "")
}