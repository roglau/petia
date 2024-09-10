package edu.bluejack23_1.petia.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityForgetPasswordBinding
import edu.bluejack23_1.petia.viewmodel.AuthViewModel

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        var isEmailValid = false

        binding.emailTF.editText?.doOnTextChanged { text, start, before, count ->
            viewModel.validateEmailForget(text.toString()) { err ->
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

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.recoverBtn.setOnClickListener{
            if(isEmailValid){
                val email = binding.emailTF.editText?.text.toString()

                viewModel.recoverAccount(email)
            }else{
                Toast.makeText(this, "Email not found!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.forgetStatusLiveData.observe(this) { success ->
            if (success) {
                var email = binding.emailTF.editText?.text.toString()
                Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Registration failed
                val errorMessage = viewModel.forgetErrorLiveData.value
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}