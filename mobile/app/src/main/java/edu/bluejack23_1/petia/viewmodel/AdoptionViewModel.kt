package edu.bluejack23_1.petia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.*
import edu.bluejack23_1.petia.repository.AdoptionRepository
import edu.bluejack23_1.petia.viewmodel.UserViewModel

class AdoptionViewModel : ViewModel() {
    private val repository: AdoptionRepository = AdoptionRepository()
    private val userViewModel : AuthViewModel = AuthViewModel()
    private val petViewModel : PetViewModel = PetViewModel()

    val userRequests = MutableLiveData<ArrayList<AdoptionRequest>>()
    val userHistories = MutableLiveData<ArrayList<AdoptionHistory>>()

    fun addAdoption(adoption: Adoption, callback: (Boolean) -> Unit) {
        repository.addAdoption(adoption) { success ->
            callback(success)
        }
    }

    fun fetchAllUserHistories(userId: String) {
        repository.fetchAllUserHistories(userId) { adoptions, exception ->
            if (exception != null) {
                userRequests.postValue(arrayListOf())
            } else if (adoptions != null) {
                val adoptionHistories = ArrayList<AdoptionHistory>()

                for (adoption in adoptions) {
                    petViewModel.getPet(adoption.petId){ pet ->
                        userViewModel.getUserByID(adoption.shelterId) { user ->
                            if (user != null) {
                                val adoptionHistory = AdoptionHistory(user, adoption, pet)
                                adoptionHistories.add(adoptionHistory)

                                if (adoptionHistories.size == adoptions.size) {
                                    userHistories.postValue(adoptionHistories)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAllRequests(userId: String) {
        repository.fetchUserAdoptions(userId) { adoptions, exception ->
            if (exception != null) {
                userRequests.postValue(arrayListOf())
            } else if (adoptions != null) {
                val adoptionRequests = ArrayList<AdoptionRequest>()

                for (adoption in adoptions) {
                    petViewModel.getPet(adoption.petId){ pet ->
                        userViewModel.getUserByID(adoption.userId) { user ->
                            if (user != null) {
                                val adoptionRequest = AdoptionRequest(user, adoption, pet)
                                adoptionRequests.add(adoptionRequest)

                                if (adoptionRequests.size == adoptions.size) {
                                    userRequests.postValue(adoptionRequests)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAdoption(adoptionId : String?, callback: (AdoptionHistory) -> Unit){
        repository.getAdoption(adoptionId) { adoption ->
            if (adoption != null) {
                petViewModel.getPet(adoption.petId){ pet ->
                    userViewModel.getUserByID(adoption.shelterId) { user ->
                        if (user != null) {
                            val adoptionHistory = AdoptionHistory(user, adoption, pet)
                            callback(adoptionHistory)
                        }
                    }
                }
            }
        }
    }

    fun approveAdoption(adoptionRequest: AdoptionRequest, callback: (Boolean) -> Unit) {
        // Update the adoption status to "Completed"
        var adoption = adoptionRequest.adoption
        if (adoption != null) {
            adoption.status = "Completed"
            repository.updateAdoption(adoption) { success ->
                if (success) {
                    // Update the pet data to indicate it has been adopted
                    val petId = adoption.petId
                    // Call a function in your repository to update the pet data
                    if (petId != null) {
                        petViewModel.updatePetAdoptionStatus(petId, true) { petSuccess ->
                            if (petSuccess) {
                                callback(true)
                            } else {
                                // Handle the error updating pet data
                                callback(false)
                            }
                        }
                    }
                } else {
                    // Handle the error updating adoption status
                    callback(false)
                }
            }
        } else {
            callback(false)
        }
    }

    // Function to reject an adoption
    fun rejectAdoption(adoptionRequest: AdoptionRequest, callback: (Boolean) -> Unit) {
        // Update the adoption status to "Rejected"
        val adoption = adoptionRequest.adoption
        if (adoption != null) {
            adoption.status = "Rejected"
            repository.updateAdoption(adoption) { success ->
                if (success) {
                    callback(true)
                } else {
                    // Handle the error updating adoption status
                    callback(false)
                }
            }
        } else {
            callback(false)
        }
    }
}
