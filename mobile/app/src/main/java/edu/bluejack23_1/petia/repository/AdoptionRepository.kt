package edu.bluejack23_1.petia.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.Adoption
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.model.User

class AdoptionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val adoptionsCollection = db.collection("adoptions")

    // Function to add an adoption
    fun addAdoption(adoption: Adoption, callback: (Boolean) -> Unit) {
        val userId = adoption.userId
        val petId = adoption.petId

        // Check if the adoption with the same userId and petId already exists
        adoptionsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("petId", petId)
            .whereEqualTo("status", "Pending")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No existing adoption with the same userId and petId, so add the adoption
                    adoptionsCollection
                        .add(adoption)
                        .addOnSuccessListener { documentReference ->
                            callback(true) // Success
                        }
                        .addOnFailureListener { e ->
                            callback(false) // Error
                        }
                } else {
                    // An adoption with the same userId and petId already exists
                    callback(false) // Already exists
                }
            }
            .addOnFailureListener { e ->
                callback(false) // Error while checking
            }
    }

    fun updateAdoption(adoption: Adoption, callback: (Boolean) -> Unit) {
        adoption.id?.let {
            adoptionsCollection
                .document(it) // Use the ID of the adoption to update
                .set(adoption)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        }
    }

    fun fetchUserAdoptions(userId: String, callback: (List<Adoption>?, Exception?) -> Unit) {
        adoptionsCollection
            .whereEqualTo("status", "Pending")
            .whereEqualTo("shelterId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val unapprovedUsers = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Adoption::class.java)
                }
                callback(unapprovedUsers, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    fun getAdoption(adoptionID: String ?= null, callback: (Adoption?) -> Unit) {
        adoptionsCollection
            .whereEqualTo(FieldPath.documentId(), adoptionID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val pet = document.toObject(Adoption::class.java)
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

    fun fetchAllUserHistories(userId: String, callback: (List<Adoption>?, Exception?) -> Unit) {
        adoptionsCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val unapprovedUsers = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Adoption::class.java)
                }
                callback(unapprovedUsers, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }
}