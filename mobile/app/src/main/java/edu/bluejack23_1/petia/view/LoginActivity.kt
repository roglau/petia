package edu.bluejack23_1.petia.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.petia.databinding.ActivityLoginBinding
import edu.bluejack23_1.petia.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.regisBtn.setOnClickListener{
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        var isEmailValid = false
        var isPasswordValid = false

        binding.emailTF.editText?.doOnTextChanged { text, _, _, _ ->
            val err = viewModel.validateEmpty(text.toString())
            if (err.isEmpty()) {
                isEmailValid = true;
                binding.emailTF.helperText = err
                binding.emailTF.error = null
            }else{
                isEmailValid = false;
                binding.emailTF.error = err
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

        binding.loginBtn.setOnClickListener {
            if (isEmailValid && isPasswordValid) {
                val email = binding.emailTF.editText?.text.toString()
                val password = binding.passTF.editText?.text.toString()

                viewModel.loginUser(email, password, applicationContext)
            } else {
                Toast.makeText(this, "Please fill in both email and password.", Toast.LENGTH_LONG).show()
            }
        }

        binding.forgetBtn.setOnClickListener{
            val intent = Intent(applicationContext, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        viewModel.loginStatusLiveData.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                binding.emailTF.error = null
                startActivity(intent)
            } else {
                val errorMessage = viewModel.loginErrorLiveData.value
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    binding.emailTF.error = "Invalid Login Credential"
                    binding.passTF.error = "Invalid Login Credential"
                }
            }
        }
    }
}