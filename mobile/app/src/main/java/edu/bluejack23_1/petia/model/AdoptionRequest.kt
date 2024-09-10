package edu.bluejack23_1.petia.model

data class AdoptionRequest(
    var user : User?,
    var adoption : Adoption?,
    var pet : Pet?
)
