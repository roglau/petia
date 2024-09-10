package edu.bluejack23_1.petia.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityHistoryDetailBinding
import edu.bluejack23_1.petia.databinding.ActivityRegisterBinding
import edu.bluejack23_1.petia.util.DateUtils
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel
import edu.bluejack23_1.petia.viewmodel.PetViewModel

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityHistoryDetailBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var  adoptionViewModel : AdoptionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        adoptionViewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.petTitle.text = "History Detail"

        val intent = intent
        val adoptionID = intent.getStringExtra("adoptionId")

        adoptionViewModel.getAdoption(adoptionID){ adoption ->
            Picasso.get().load(adoption.pet?.petImage).into(binding.petDetailImage)
            binding.petName.text = adoption.pet?.petName
            binding.shelter.text = "From: " + adoption.shelter?.username

            val statusText = adoption.adoption?.status
            binding.adoptStatus.text = statusText

            when (statusText) {
                "Pending" -> binding.adoptStatus.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_yellow)) // Set to yellow
                "Rejected" -> binding.adoptStatus.setTextColor(Color.RED)    // Set to red
                "Completed" -> binding.adoptStatus.setTextColor(Color.GREEN)  // Set to green
                else -> binding.adoptStatus.setTextColor(Color.BLACK) // Set a default color for other statuses
            }

            binding.adoptDate.text = "Date: " + adoption.adoption?.date?.let {
                DateUtils.formatDateAsDayMonthYear(
                    it
                )
            }

            binding.animal.text = "Animal: " + adoption.pet?.animalType
            binding.breed.text = "Breed: " + adoption.pet?.petBreed
            binding.age.text = "Age: " + adoption.pet?.petAge
            binding.color.text = "Color: "+ adoption.pet?.petColor
            binding.size.text = "Size: " +adoption.pet?.petSize

            binding.shelterBtn.setOnClickListener{
                adoption.shelter?.id?.let { it1 -> sharedPreferencesManager.setChosenUserId(it1) }
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

        }
    }

}