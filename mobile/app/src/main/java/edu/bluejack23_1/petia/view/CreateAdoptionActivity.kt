package edu.bluejack23_1.petia.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityCreateAdoptionBinding
import edu.bluejack23_1.petia.databinding.ActivityLoginBinding
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.PetViewModel

class CreateAdoptionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCreateAdoptionBinding
    private lateinit var viewModel: PetViewModel
    private lateinit var sharedPreferences : SharedPreferencesManager
    private var selectedImage: Uri? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAdoptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(PetViewModel::class.java)

        sharedPreferences = SharedPreferencesManager(applicationContext)

        val animal = arrayOf("Dog", "Cat")
        val size = arrayOf("Small", "Medium", "Large")
        val age = arrayOf("Young", "Young-Old", "Old")
        val gender = arrayOf("Male", "Female")

        val animalAdapter = ArrayAdapter(this, R.layout.list_item, animal)

        val sizeAdapter = ArrayAdapter(this, R.layout.list_item, size)

        val ageAdapter = ArrayAdapter(this, R.layout.list_item, age)

        val genderAdapter = ArrayAdapter(this, R.layout.list_item, gender)

        ( binding.animalDD.editText as? AutoCompleteTextView)?.setAdapter(animalAdapter)
        (binding.sizeDD.editText as? AutoCompleteTextView)?.setAdapter(sizeAdapter)
        (binding.ageDD.editText as? AutoCompleteTextView)?.setAdapter(ageAdapter)
        (binding.genderDD.editText as? AutoCompleteTextView)?.setAdapter(genderAdapter)

        binding.topAppBar.setNavigationOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("selectedPage", 0)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        var isAnimalValid = true
        var isNameValid = true
        var isSizeValid = true
        var isBreedValid = true
        var isColorValid = true
        var isAgeValid = true
        var isGenderValid = true
        var isDescValid = true

        binding.animalDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isAnimalValid = true;
                binding.animalDD.helperText = err
                binding.animalDD.error = null
            }else{
                isAnimalValid = false;
                binding.animalDD.error = err
            }
        }

        binding.genderDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isGenderValid = true;
                binding.genderDD.helperText = err
                binding.genderDD.error = null
            }else{
                isGenderValid = false;
                binding.genderDD.error = err
            }
        }

        binding.nameDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isNameValid = true;
                binding.nameDD.helperText = err
                binding.nameDD.error = null
            }else{
                isNameValid = false;
                binding.nameDD.error = err
            }
        }
        binding.ageDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isAgeValid = true;
                binding.ageDD.helperText = err
                binding.ageDD.error = null
            }else{
                isAgeValid = false;
                binding.ageDD.error = err
            }
        }
        binding.breedDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isBreedValid = true;
                binding.breedDD.helperText = err
                binding.breedDD.error = null
            }else{
                isBreedValid = false;
                binding.breedDD.error = err
            }
        }
        binding.sizeDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isSizeValid = true;
                binding.sizeDD.helperText = err
                binding.sizeDD.error = null
            }else{
                isSizeValid = false;
                binding.sizeDD.error = err
            }
        }
        binding.colorDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isColorValid = true;
                binding.colorDD.helperText = err
                binding.colorDD.error = null
            }else{
                isColorValid = false;
                binding.colorDD.error = err
            }
        }
        binding.descDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isDescValid = true;
                binding.descDD.helperText = err
                binding.descDD.error = null
            }else{
                isDescValid = false;
                binding.descDD.error = err
            }
        }

        binding.createBtn.setOnClickListener{
            if(isAnimalValid && isAgeValid && isNameValid && isBreedValid && isSizeValid && isColorValid && isGenderValid && isDescValid && selectedImage != null){
                val animal = binding.animalDD.editText?.text.toString()
                val name = binding.nameDD.editText?.text.toString()
                val breed = binding.breedDD.editText?.text.toString()
                val color = binding.colorDD.editText?.text.toString()
                val size = binding.sizeDD.editText?.text.toString()
                val age = binding.ageDD.editText?.text.toString()
                val gender = binding.genderDD.editText?.text.toString()
                val desc = binding.descDD.editText?.text.toString()

                val pet = Pet(
                    animalType = animal,
                    petName = name,
                    petBreed = breed,
                    petColor = color,
                    petSize = size,
                    petAge = age,
                    petGender = gender,
                    petImage = selectedImage.toString(),
                    petDescription = desc,
                    shelterID = sharedPreferences.getUserId(),
                    adopted = false
                )

                viewModel.addPet(pet) { success ->
                    if (success) {
                        Toast.makeText(this, "Pet added!", Toast.LENGTH_LONG).show()
                        val resultIntent = Intent()
                        resultIntent.putExtra("selectedPage", 0)

                        // Set the result to indicate success
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error adding to db!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.cancelBtn.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("selectedPage", 0)

            // Set the result to indicate success
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.chooseFileButton.setOnClickListener{
            showProfileDialog()
        }
    }

    private fun showProfileDialog() {
        if (!isFinishing) {
            dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.fragment_change_profile)

            val closeBtn = dialog?.findViewById<Button>(R.id.closeChangeProfile)
            val chooseImageButton: Button? = dialog?.findViewById(R.id.chooseImageButton)
            val uploadButton: Button? = dialog?.findViewById(R.id.uploadButton)
            uploadButton?.visibility = View.GONE
            val imagePreview: ImageView? = dialog?.findViewById(R.id.imagePreview)

            imagePreview?.setBackgroundResource(R.drawable.circle_background)
            if(selectedImage != null){
                Picasso.get().load(selectedImage).into(dialog?.findViewById(R.id.imagePreview))
            }

            chooseImageButton?.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT

                startActivityForResult(intent, 100)

                if(selectedImage != null){
                    binding.fileSelectionLayout.helperText = null
                }else{
                    binding.fileSelectionLayout.helperText = "Required*"
                }
            }

            closeBtn?.setOnClickListener {
                dialog?.dismiss()
            }

            dialog?.show()
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog?.window?.setGravity(Gravity.BOTTOM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImage = data?.data!!

            val imagePreview: ImageView? = dialog?.findViewById(R.id.imagePreview)
            Log.d("File", selectedImage.toString())
            Picasso.get().load(selectedImage).into(imagePreview)
        }
    }
}