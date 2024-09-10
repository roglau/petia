package edu.bluejack23_1.petia.model

data class Pet(var animalType: String ?= null, var petAge: String? = null, var petName: String ?= null,
               var petBreed: String ?= null, var petColor: String ?= null, var petImage: String ?= null,
               var petSize: String ?= null, var documentID: String ?= null, var petGender: String ?= null
               , var petDescription : String ?= null, var adopted : Boolean ?= null
                ,var shelterID : String ?= null)