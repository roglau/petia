package edu.bluejack23_1.petia.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.R.*
import edu.bluejack23_1.petia.databinding.FragmentHomeBinding
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.MyAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<Pet>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: WishlistViewModel
    private lateinit var binding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)

        binding.editTextTextPersonName2.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val sharedPreferencesManager = SharedPreferencesManager(requireContext())
                sharedPreferencesManager.setSearch(binding.editTextTextPersonName2.text.toString())

                val activity = requireActivity()

                if(activity is MainActivity) {
                    activity.changeShow(2)
                    activity.changeFragment(PetListFragment())
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        recyclerView = binding.recyclerView
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

        myAdapter = MyAdapter(petArrayList, viewModel, requireContext(), someActivityResultLauncher)
        recyclerView.adapter = myAdapter

        eventChangeListener()

        return binding.root
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("pets").whereEqualTo("adopted",false).limit(5).addSnapshotListener(object: EventListener<QuerySnapshot> {
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

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(edu.bluejack23_1.petia.R.id.fragmentContainer, fragment)
        transaction.commit()
    }

}