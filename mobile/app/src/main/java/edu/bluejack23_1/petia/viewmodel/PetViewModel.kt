package edu.bluejack23_1.petia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.Filter
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.repository.PetRepository
import edu.bluejack23_1.petia.util.MyAdapter

class PetViewModel : ViewModel() {
    private val petRepository = PetRepository()
    val petListLiveData = MutableLiveData<List<Pet>>()

    val filterLiveData = MutableLiveData<Filter>()

    private val wishListViewModel = WishlistViewModel()
    val petNWishlistLiveData = MutableLiveData<ArrayList<PetNWishlist>>()

    fun validateEmpty(text: String) : String {
        if(text.isEmpty()){
            return "Required*"
        }else{
            return ""
        }
    }

    fun addPet(pet: Pet, callback: (Boolean) -> Unit) {
        petRepository.addPet(pet, callback)
    }

    fun updateFilter(filter: Filter?) {
        filterLiveData.value = filter
    }

    fun getPet(petID: String ?= null, callback: (Pet?) -> Unit) {
        petRepository.getPet(petID, callback)
    }

    fun updatePetAdoptionStatus(petId: String, isAdopted: Boolean, callback: (Boolean) -> Unit) {
        petRepository.updatePetAdoptionStatus(petId, isAdopted, callback)
    }

    fun loadData(limit: Int, userID: String?, filter: Filter?) {
        val petNWishlists = ArrayList<PetNWishlist>()
        petRepository.getAllPets(limit, filter) { pets ->
            if(pets.isEmpty()){
                petNWishlistLiveData.postValue(arrayListOf())
            }else{
                for(pet in pets){
                    wishListViewModel.getWishlist(pet.documentID, userID) { wishlists ->
                        val isLiked = wishlists.isNotEmpty()
                        val petNWishlist = PetNWishlist(
                            pet.animalType,
                            pet.petAge,
                            pet.petName,
                            pet.petBreed,
                            pet.petColor,
                            pet.petImage,
                            pet.petSize,
                            pet.documentID,
                            pet.petGender,
                            isLiked
                        )
                        petNWishlists.add(petNWishlist)

                    if (petNWishlists.size == pets.size) {
                        println(pet.documentID)
                        petNWishlistLiveData.postValue(petNWishlists)
                        }
                    }
                }
            }
        }
    }


    fun getPetNWishlist(userID: String?, limit: Int){
        val petNWishlists = ArrayList<PetNWishlist>()

        petRepository.observePets { pets ->
            for(pet in pets){
                wishListViewModel.getWishlist(pet.documentID, userID) { wishlists ->
                    val isLiked = wishlists.isNotEmpty()
                    val petNWishlist = PetNWishlist(
                        pet.animalType,
                        pet.petAge,
                        pet.petName,
                        pet.petBreed,
                        pet.petColor,
                        pet.petImage,
                        pet.petSize,
                        pet.documentID,
                        pet.petGender,
                        isLiked
                    )
                    petNWishlists.add(petNWishlist)

                    if (petNWishlists.size == pets.size) {
                        // All pets have been processed
                        petNWishlistLiveData.postValue(petNWishlists)
                    }
                }
            }
        }
    }

    fun observePets(callback: (List<Pet>) -> Unit) {
        petRepository.observePets { pets ->
            petListLiveData.postValue(pets)
        }
    }

    fun eventChangeListener(myAdapter: MyAdapter) {
        petRepository.eventChangeListener(myAdapter)
    }
}