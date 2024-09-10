package edu.bluejack23_1.petia.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.util.WishlistAdapter
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel

class WishlistActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<Pet>
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)

        recyclerView = findViewById(R.id.wishlistRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.hasFixedSize()

        petArrayList = arrayListOf()
//        wishlistAdapter = WishlistAdapter(petArrayList, viewModel, applicationContext)
        recyclerView.adapter = wishlistAdapter

        eventChangeListener(applicationContext)
    }

    private fun eventChangeListener(context: Context) {
        db = FirebaseFirestore.getInstance()
        val sharedPreferencesManager = SharedPreferencesManager(context)
        val userID = sharedPreferencesManager.getUserId()

        db.collection("wishlists")
            .whereEqualTo("userID", userID)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(wishlistSnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    if (wishlistSnapshot != null && !wishlistSnapshot.isEmpty) {
                        val petIDs = ArrayList<String>()
                        for (document in wishlistSnapshot.documents) {
                            val petID = document.getString("petID")
                            if (petID != null) {
                                petIDs.add(petID)
                            }
                        }
                        if (petIDs.isNotEmpty()) {
                            db.collection("pets")
                                .whereIn(FieldPath.documentId(), petIDs)
                                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                                    override fun onEvent(petSnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
                                        if (error != null) {
                                            Log.e("Firestore Error", error.message.toString())
                                            return
                                        }
                                        if (petSnapshot != null && !petSnapshot.isEmpty) {
                                            val newPets = ArrayList<Pet>()
                                            for (dc: DocumentChange in petSnapshot.documentChanges) {
                                                if (dc.type == DocumentChange.Type.ADDED) {
                                                    val pet = dc.document.toObject(Pet::class.java)
                                                    pet.documentID = dc.document.id
                                                    petArrayList.add(pet)
                                                    newPets.add(pet)
                                                }
                                            }
                                            wishlistAdapter.notifyDataSetChanged()

                                            for (pet in newPets) {
                                                Log.d("FetchedPet", "Pet: ${pet.petName}, Age: ${pet.petAge}, Type: ${pet.animalType}, Breed: ${pet.petBreed}, Color: ${pet.petColor}, Size: ${pet.petSize}, Image: ${pet.petImage}")
                                            }
                                        }
                                    }
                                })
                        }
                    }
                }
            })
    }
}