package edu.bluejack23_1.petia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.Wishlist
import edu.bluejack23_1.petia.repository.WishlistRepository

class WishlistViewModel : ViewModel() {
    private val wishlistRepository = WishlistRepository()

    fun addToWishlist(wishlist: Wishlist, callback: (Boolean, String) -> Unit) {
        wishlistRepository.addToWishlist(wishlist) { success, message ->
            if (success) {
                callback(true, message)
            } else {
                callback(false, message)
            }
        }
    }

    fun getWishlist(petID: String ?= null, userID: String ?= null, callback: (List<Wishlist>) -> Unit) {
        wishlistRepository.getWishlist(petID, userID) { wishlists ->
            callback(wishlists)
        }
    }

    fun deleteWishlist(petID: String ?= null, userID: String ?= null, callback: (Boolean, String) -> Unit) {
        wishlistRepository.deleteWishlist(petID, userID, callback)
    }
}