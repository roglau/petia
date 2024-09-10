package edu.bluejack23_1.petia.view

import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Adoption
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.*


class PetDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: PetViewModel
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var  adoptionViewModel : AdoptionViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        adoptionViewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)

        val msgBtn = findViewById<ImageButton>(R.id.messengerBtn)

        if(sharedPreferencesManager.getUserRole() != "Customer"){
            val myLayout = findViewById<LinearLayout>(R.id.adoptBtnCon)
            myLayout.visibility = View.GONE
            msgBtn.visibility = View.GONE
        }

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            finish()
        }
        val adoptBtn = findViewById<Button>(R.id.adoptBtn)

        val intent = intent
        val petID = intent.getStringExtra("petID")

        adoptBtn.setOnClickListener{
            var shelterId : String?
            val userId = sharedPreferencesManager.getUserId()
            val status = "Pending"
            val currentDate = Date()

            val dateFormatPattern = "yyyy-MM-dd HH:mm:ss"
            val dateFormat = SimpleDateFormat(dateFormatPattern)
            val dateString = dateFormat.format(currentDate)

            viewModel.getPet(petID) { pet ->
                shelterId = pet?.shelterID
                val adoption = Adoption(null, userId, shelterId, petID, status, dateString)
                adoptionViewModel.addAdoption(adoption){ success ->
                    if (success) {
                        Toast.makeText(this, "Adopt request sent!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "You already applied before!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }

        }

        viewModel = ViewModelProvider(this).get(PetViewModel::class.java)



        msgBtn.setOnClickListener{
            val resultsIntent = Intent()
            resultsIntent.putExtra("messenger", 1)
            setResult(Activity.RESULT_OK, resultsIntent)
            finish()
        }



        viewModel.getPet(petID) { pet ->
            if (pet != null) {
                val imageView = findViewById<ImageView>(R.id.petDetailImage)
                val petAndShelterName = findViewById<TextView>(R.id.petAndShelterName)
                val petBreed = findViewById<TextView>(R.id.petBreed)
                val petColor = findViewById<TextView>(R.id.petColor)
                val petAge = findViewById<TextView>(R.id.petAge)
                val petGender = findViewById<TextView>(R.id.petGender)
                val petSize = findViewById<TextView>(R.id.petSize)
                val petTitle = findViewById<TextView>(R.id.petTitle)
                val descTitle = findViewById<TextView>(R.id.descTitle)
                val petDesc = findViewById<TextView>(R.id.desc)
                val shelterBtn = findViewById<ImageButton>(R.id.shelterBtn)

                Picasso.get().load(pet.petImage).into(imageView)
                petAndShelterName.text = pet.petName+" - LCAS Shelter" // Nanti concat sama sheltername
                petBreed.text = "Breed: "+pet.petBreed
                petColor.text = "Color: "+pet.petColor
                petAge.text = pet.petAge
                petGender.text = pet.petGender
                petSize.text = pet.petSize
                petTitle.text = pet.petName
                descTitle.text = "About " + pet.petName
                petDesc.text = pet.petDescription

                shelterBtn.setOnClickListener{
                    pet.shelterID?.let { it1 -> sharedPreferencesManager.setChosenUserId(it1) }
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}