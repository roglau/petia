package edu.bluejack23_1.petia.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Adoption(
    @DocumentId
    val id: String?,
    val userId: String?,
    val shelterId: String?,
    val petId: String?,
    var status: String?,
    var date : String?
){
    constructor() : this("", "", "", "", "", "")
}
