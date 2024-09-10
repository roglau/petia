package edu.bluejack23_1.petia.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.MyAdapter
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<Pet>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.hasFixedSize()

        petArrayList = arrayListOf()
//        myAdapter = MyAdapter(petArrayList, viewModel, applicationContext)
        recyclerView.adapter = myAdapter

        eventChangeListener()
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("pets").limit(5).addSnapshotListener(object: EventListener<QuerySnapshot> {
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
}