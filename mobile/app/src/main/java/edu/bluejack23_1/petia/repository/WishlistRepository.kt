package edu.bluejack23_1.petia.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.Wishlist

class WishlistRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addToWishlist(wishlist: Wishlist, onComplete: (Boolean, String) -> Unit) {
        val wishlistsCollection = db.collection("wishlists")

        // Add a new document with a generated ID
        wishlistsCollection
            .add(wishlist)
            .addOnSuccessListener { documentReference ->
                // Document successfully added
                onComplete(true, "Insert Successful!")
            }
            .addOnFailureListener { e ->
                // Handle errors here
                onComplete(false, "Insert to Firestore Failed!")
            }
    }

    fun getWishlist(petID: String ?= null, userID: String ?= null, callback: (List<Wishlist>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val wishlists = mutableListOf<Wishlist>()

        db.collection("wishlists")
            .whereEqualTo("petID", petID)
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val wishlist = document.toObject(Wishlist::class.java)
                    wishlists.add(wishlist)
                }
                callback(wishlists)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting wishlists", exception)
                callback(emptyList())
            }
    }

    fun deleteWishlist(petID: String ?= null, userID: String ?= null, callback: (Boolean, String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val wishlistsCollection = db.collection("wishlists")

        wishlistsCollection
            .whereEqualTo("petID", petID)
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    wishlistsCollection.document(document.id).delete()
                }
                callback(true, "Wishlists deleted successfully")
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error deleting wishlists: ${exception.message}"
                Log.e("Firestore Error", errorMessage)
                callback(false, errorMessage)
            }
    }

}