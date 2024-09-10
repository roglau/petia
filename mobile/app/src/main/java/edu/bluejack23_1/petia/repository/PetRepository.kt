package edu.bluejack23_1.petia.repository

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.util.MyAdapter

class PetRepository {
    private val db = FirebaseFirestore.getInstance()
    private val petArrayList = ArrayList<Pet>()
    private val storage = FirebaseStorage.getInstance()

    fun addPet(pet: Pet, callback: (Boolean) -> Unit) {
        val uri = Uri.parse(pet.petImage)
        val storageRef = storage.getReferenceFromUrl("gs://petia-9fa03.appspot.com/")
        val filename = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val imageRef = storageRef.child("$filename")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()

                        val petData = mapOf(
                            "animalType" to pet.animalType,
                            "petAge" to pet.petAge,
                            "petName" to pet.petName,
                            "petBreed" to pet.petBreed,
                            "petColor" to pet.petColor,
                            "petImage" to imageUrl,
                            "petSize" to pet.petSize,
                            "petGender" to pet.petGender,
                            "petDescription" to pet.petDescription,
                            "shelterID" to pet.shelterID,
                            "adopted" to pet.adopted
                        )

                        // Add the pet data to the Firestore collection
                        db.collection("pets").add(petData)
                            .addOnSuccessListener { documentReference ->
                                // The pet was added successfully
                                val petId = documentReference.id
                                callback(true)
                            }
                            .addOnFailureListener { e ->
                                // Handle the error in case of failure
                                callback(false)
                            }
                    }
            }
            .addOnFailureListener { exception ->
                callback(false)
            }
    }

    fun getAllPets(limit: Int, filter: edu.bluejack23_1.petia.model.Filter?, callback: (ArrayList<Pet>) -> Unit) {
        var query = db.collection("pets")
            .limit(limit.toLong()).whereEqualTo("adopted",false)

        // Apply filters based on the values in the 'filter' object
        if (filter != null) {
            if (filter.animal?.isNotEmpty() == true && filter.type?.isNotEmpty() == true) {
                query = query.whereEqualTo("animalType", listOf(filter.animal, filter.type))
            }else if(filter.animal?.isNotEmpty() == true){
//                println("Testing0 :${filter.animal}")
                query = query.whereEqualTo("animalType", filter.animal)
            }else if(filter.type?.isNotEmpty() == true){
//                println("Testing1 :${filter.type}")
                query = query.whereEqualTo("animalType", filter.type)
            }

            if (filter.size?.isNotEmpty() == true) {
                query = query.whereEqualTo("petSize", filter.size)
            }

            if (filter.age?.isNotEmpty() == true) {
                query = query.whereEqualTo("petAge", filter.age)
            }

            if (filter.color?.isNotEmpty() == true) {
                query = query.whereEqualTo("petColor", filter.color)
            }

            if (filter.breed?.isNotEmpty() == true) {
                query= query.whereEqualTo("petBreed", filter.breed)
            }


        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val newPets = ArrayList<Pet>()
                println("Start Count")
                for (document in querySnapshot.documents) {
                    println("Count")
                    val pet = document.toObject(Pet::class.java)
                    if (pet != null) {
                        pet.documentID = document.id
                        newPets.add(pet)
                    }
                }

                callback(newPets)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting pet data", exception)
            }
    }



    fun observePets(callback: (List<Pet>) -> Unit) {
        db.collection("pets")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    val newPets = ArrayList<Pet>()
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val pet = dc.document.toObject(Pet::class.java)
                            pet.documentID = dc.document.id
                            newPets.add(pet)
                        }
                    }
                    petArrayList.addAll(newPets)
                    callback(petArrayList)
                }
            })
    }

    fun eventChangeListener(myAdapter: MyAdapter) {
        db.collection("pets").limit(5).addSnapshotListener(object: EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                val newPets = ArrayList<Pet>()
                for(dc : DocumentChange in value?.documentChanges!!) {
                    if(dc.type == DocumentChange.Type.ADDED) {
                        val pet = dc.document.toObject(Pet::class.java)
                        pet.documentID = dc.document.id
                        petArrayList.add(pet)
                        newPets.add(pet)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    fun getPet(petID: String ?= null, callback: (Pet?) -> Unit) {
        db.collection("pets")
            .whereEqualTo(FieldPath.documentId(), petID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val pet = document.toObject(Pet::class.java)
                    callback(pet)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting pet data", exception)
                callback(null)
            }
    }

    fun updatePetAdoptionStatus(petId: String, isAdopted: Boolean, callback: (Boolean) -> Unit) {
        db.collection("pets")
            .document(petId) // Use the ID of the pet to update
            .update("adopted", isAdopted)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
            }
    }
}