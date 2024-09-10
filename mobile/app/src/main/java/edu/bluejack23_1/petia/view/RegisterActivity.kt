package edu.bluejack23_1.petia.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityRegisterBinding
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val roles = arrayOf("Customer", "Shelter") // Replace with your desired role values
        val adapter = ArrayAdapter(this, R.layout.list_item, roles)

        (binding.roleDD.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        var isNameValid = false
        var isEmailValid = false
        var isUsernameValid = false
        var isPasswordValid = false
        var isConfirmPasswordValid = false
        var isPhoneNumberValid = false
        var isRoleValid = false

        binding.emailTF.editText?.doOnTextChanged { text, start, before, count ->
            viewModel.validateEmail(text.toString()) { err ->
                if (err.isEmpty()) {
                    isEmailValid = true
                    binding.emailTF.helperText = err
                    binding.emailTF.error = null
                } else {
                    isEmailValid = false
                    binding.emailTF.error = err
                }
            }
        }

        binding.nameTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isNameValid = true;
                binding.nameTF.helperText = err
                binding.nameTF.error = null
            }else{
                isNameValid = false;
                binding.nameTF.error = err
            }
        }

        binding.usernameTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isUsernameValid = true;
                binding.usernameTF.helperText = err
                binding.usernameTF.error = null
            }else{
                isUsernameValid = false;
                binding.usernameTF.error = err
            }
        }

        binding.passTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isPasswordValid = true;
                binding.passTF.helperText = err
                binding.passTF.error = null
            }else{
                isPasswordValid = false;
                binding.passTF.error = err
            }
        }

        binding.confpassTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateConfPassword(text.toString(), binding.passTF.editText?.text.toString())
            if (err.isEmpty()) {
                isConfirmPasswordValid = true;
                binding.confpassTF.helperText = err
                binding.confpassTF.error = null
            }else{
                isConfirmPasswordValid = false;
                binding.confpassTF.error = err
            }
        }

        binding.phoneTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isPhoneNumberValid = true;
                binding.phoneTF.helperText = err
                binding.phoneTF.error = null
            }else{
                isPhoneNumberValid = false;
                binding.phoneTF.error = err
            }
        }

        binding.roleDD.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isRoleValid = true;
                binding.roleDD.helperText = err
                binding.roleDD.error = null
            }else{
                isRoleValid = false;
                binding.roleDD.error = err
            }
        }

        binding.regisBtn.setOnClickListener {

            if(isNameValid && isEmailValid && isUsernameValid && isPhoneNumberValid && isPasswordValid && isConfirmPasswordValid && isRoleValid){
                val name = binding.nameTF.editText?.text.toString()
                val username = binding.usernameTF.editText?.text.toString()
                val email = binding.emailTF.editText?.text.toString()
                val password = binding.passTF.editText?.text.toString()
                val phoneNumber = binding.phoneTF.editText?.text.toString()
                val role = binding.roleDD.editText?.text.toString()
                val profileImage = "https://firebasestorage.googleapis.com/v0/b/petia-9fa03.appspot.com/o/profile.png?alt=media&token=ec630474-d620-47e3-9053-ab16c88cbab0&_gl=1*10rxyiy*_ga*MTkxMzg1NDA0NS4xNjk3MDk1ODc3*_ga_CW55HF8NVT*MTY5ODExOTQ1MS4zMC4xLjE2OTgxMTk0ODEuMzAuMC4w"

                val status = role == "Customer"
                // Create a User instance with the data
                val user = User(id = "",fullname = name, username = username, password = password, email = email, phone = phoneNumber, role = role, status = status, profileImage = profileImage)

                // Call the registerUser function in the AuthViewModel
                viewModel.registerUser(user)
            }else{
                Toast.makeText(this, "Some field still error", Toast.LENGTH_LONG).show()
            }

        }

        binding.loginBtn.setOnClickListener{
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        viewModel.registrationStatusLiveData.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Registration failed
                val errorMessage = viewModel.regisErrorLiveData.value
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}