package edu.bluejack23_1.petia.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentWishlistBinding
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.MyAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.util.WishlistAdapter
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel

class WishlistFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<Pet>
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: WishlistViewModel
    private lateinit var binding : FragmentWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentWishlistBinding.inflate(inflater, container, false)

            viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)

            recyclerView = binding.wishlistRecyclerView
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.hasFixedSize()

            petArrayList = arrayListOf()

            var someActivityResultLauncher: ActivityResultLauncher<Intent>

            someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val messenger = data?.getIntExtra("messenger", 0)

                    if (messenger == 1) {
                        changeFragment(MessengerFragment())
                    }else{
                        changeFragment(ProfileFragment())
                    }
                }
            }
            wishlistAdapter = WishlistAdapter(petArrayList, viewModel, requireContext(), someActivityResultLauncher)
            recyclerView.adapter = wishlistAdapter

            eventChangeListener(requireContext())

            return binding.root
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
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