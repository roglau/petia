package edu.bluejack23_1.petia.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.repository.UserRepository
import edu.bluejack23_1.petia.util.SharedPreferencesManager


class UserViewModel() : ViewModel() {

    private val userRepository = UserRepository()
    val unapprovedShelter = MutableLiveData<ArrayList<User>>()

    fun approveUser(user: User, callback: (Boolean) -> Unit) {
        userRepository.approveUser(user) { success , msg->
            if (success) {
                callback(true)
            }else{
                callback(false)
            }
        }
    }

    fun removeUser(user: User, callback: (Boolean) -> Unit) {
        userRepository.removeUser(user) { success ->
            if (success) {
                callback(true)
            }else{
                callback(false)
            }
        }
    }


    fun getAllUnapprovedUsers() {
        userRepository.getAllUnapprovedUsers { unapprovedUsersList, exception ->
            if (exception == null) {
                unapprovedShelter.postValue(ArrayList(unapprovedUsersList))
            } else {
                unapprovedShelter.postValue(arrayListOf())
            }
        }
    }


}