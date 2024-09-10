package edu.bluejack23_1.petia.view

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityLoginBinding
import edu.bluejack23_1.petia.databinding.ActivityMainBinding
import edu.bluejack23_1.petia.util.MyAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startCreateAdoptionActivity: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigation.show(0)
        changeFragment(HomeFragment())

        binding.topAppBar.setNavigationOnClickListener {
            changeFragment(HomeFragment())
        }

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notification -> {
                    if(sharedPreferencesManager.getUserRole() == "Admin" || sharedPreferencesManager.getUserRole() == "Shelter")
                        changeFragment(NotificationFragment())
                    true
                }
                R.id.chat -> {
                    changeFragment(MessengerFragment())
                    true
                }
                R.id.profile -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }
        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        if(sharedPreferencesManager.getUserRole() == "Customer"){
            binding.bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.wishlist_icon))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_pets))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_history))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.ic_person))
        }else if(sharedPreferencesManager.getUserRole() == "Shelter"){
            binding.bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_add_pet))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_approve))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_person))
        }else {
            binding.bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_approve))
            binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_person))
        }

        startCreateAdoptionActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedPage = data?.getIntExtra("selectedPage", -1)

                if (selectedPage != -1) {
                    if (selectedPage != null) {
                        changeShow(selectedPage)
                    }
                }
            }
        }


        binding.bottomNavigation.setOnClickMenuListener{ item ->
            if(sharedPreferencesManager.getUserRole() == "Customer"){
                when (item.id) {
                    0 -> changeFragment(HomeFragment())
                    1 -> changeFragment(WishlistFragment())
                    2 -> changeFragment(PetListFragment())
                    3 -> changeFragment(HistoryFragment())
                    4 -> changeFragment(ProfileFragment())
                }
            }else if(sharedPreferencesManager.getUserRole() == "Shelter"){
                when (item.id) {
                    0 -> changeFragment(HomeFragment())
                    1 -> {
                        val intent = Intent(applicationContext, CreateAdoptionActivity::class.java)
                        startCreateAdoptionActivity.launch(intent)
                    }
                    2 -> changeFragment(ApproveAdoptionFragment())
                    3 -> changeFragment(ProfileFragment())
                }
            }else {
                when (item.id) {
                    0 -> changeFragment(HomeFragment())
                    1 -> changeFragment(ApproveShelterFragment())
                    2 -> changeFragment(ProfileFragment())
                }
            }
        }

//        adoptionViewModel.goProfile.observe(this) { navigate ->
//            if (navigate) {
//                // Change the fragment in the MainActivity
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.fragmentContainer, newFragment)
//                transaction.addToBackStack(null) // Optional, to allow back navigation
//                transaction.commit()
//
//                // Reset the value in the ViewModel to avoid repeated fragment changes
//                adoptionViewModel.goProfile = false
//            }
//        }
    }

    fun changeShow(idx : Int){
        binding.bottomNavigation.show(idx)
    }

    fun changeFragment(fragment: Fragment) {
        if(fragment is ProfileFragment) {
            val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            sharedPreferencesManager.getUserId()
                ?.let { sharedPreferencesManager.setChosenUserId(it) }
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}