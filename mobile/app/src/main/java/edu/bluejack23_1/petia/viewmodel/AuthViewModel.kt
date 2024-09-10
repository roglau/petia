package edu.bluejack23_1.petia.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.repository.UserRepository
import edu.bluejack23_1.petia.util.SharedPreferencesManager

class AuthViewModel() : ViewModel() {

    val registrationStatusLiveData = MutableLiveData<Boolean>()
    val loginStatusLiveData = MutableLiveData<Boolean>()
    val forgetStatusLiveData = MutableLiveData<Boolean>()

    val loginErrorLiveData = MutableLiveData<String>()
    val regisErrorLiveData = MutableLiveData<String>()
    val forgetErrorLiveData = MutableLiveData<String>()

    private val userRepository = UserRepository()

    fun registerUser(user : User) {
        userRepository.registerUser(user) { success, message ->
            if (success) {
                registrationStatusLiveData.value = true
                regisErrorLiveData.value = ""
            } else {
                registrationStatusLiveData.value = false
                regisErrorLiveData.value = message
            }
        }
    }

    fun loginUser(email : String, password:String, context: Context ) {
        var status : Boolean = false
        userRepository.getUserByEmail(email) { user ->
            if (user != null) {
                status = user.status
            }

            if(status){
                userRepository.loginUser(email, password) { success, message ->
                    if (success) {
                        val sharedPreferencesManager = SharedPreferencesManager(context)
                        sharedPreferencesManager.setUserId(user?.id ?: "")
                        sharedPreferencesManager.setUserLoggedIn(true)
                        sharedPreferencesManager.setUserRole(user?.role ?: "")

                        loginStatusLiveData.value = true
                        loginErrorLiveData.value = ""
                    } else {
                        loginStatusLiveData.value = false
                        loginErrorLiveData.value = message
                    }
                }
            }else{
                loginStatusLiveData.value = false
                loginErrorLiveData.value = "Email and password not found!"
            }
        }
    }

    fun recoverAccount(email : String){
        userRepository.recoverEmail(email) { success, message ->
            if (success) {
                forgetStatusLiveData.value = true
                forgetErrorLiveData.value = ""
            } else {
                forgetStatusLiveData.value = false
                forgetErrorLiveData.value = message
            }
        }
    }

    fun validateEmail(email: String, onValidationResult: (String) -> Unit) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            var msg = ""
            userRepository.isEmailRegistered(email) { isRegistered ->
                msg = if (isRegistered) {
                    "Email is already registered."
                } else {
                    ""
                }
                onValidationResult(msg)
            }
        } else if (email.isEmpty()) {
            onValidationResult("Required*")
        } else {
            onValidationResult("Invalid email format!")
        }
    }

    fun validateEmailForget(email: String, onValidationResult: (String) -> Unit) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            var msg = ""
            userRepository.isEmailRegistered(email) { isRegistered ->
                msg = if (isRegistered) {
                    ""
                } else {
                    "Email not found"
                }
                onValidationResult(msg)
            }
        } else if (email.isEmpty()) {
            onValidationResult("Required*")
        } else{
            onValidationResult("Invalid email format!")
        }
    }

    fun validateEmpty(text: String) : String {
        if(text.isEmpty()){
            return "Required*"
        }else{
            return ""
        }
    }

    fun validateConfPassword(confpassword : String,password: String) : String {
        if(!confpassword.isEmpty() && confpassword.equals(password)){
            return ""
        }else if(!confpassword.isEmpty()){
            return "Confirm password must match password!"
        }else{
            return "Required*"
        }
    }

    fun getUserByID(userID: String?, onComplete: (User?) -> Unit) {
        if (userID != null) {
            userRepository.getUserByID(userID) { user ->
                onComplete(user)
            }
        } else {
            onComplete(null)
        }
    }

    fun logoutUser(context: Context) {
        userRepository.logoutUser { success ->
            val sharedPreferencesManager = SharedPreferencesManager(context)
            sharedPreferencesManager.clearPreferences()
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, confPassword: String, onComplete: (Boolean, String) -> Unit) {
        if (oldPassword.isEmpty()) {
            onComplete(false, "Old password cannot be empty.")
            return
        }
        if (newPassword.isEmpty()) {
            onComplete(false, "New password cannot be empty.")
            return
        }
        if(oldPassword == newPassword) {
            onComplete(false, "Old password and new password cannot be the same.")
            return
        }
        if (newPassword != confPassword) {
            onComplete(false, "New password and confirm password must match.")
            return
        }
        userRepository.validateOldPassword(oldPassword) { isOldPasswordValid : Boolean ->
            if (isOldPasswordValid) {
                userRepository.changePassword(oldPassword, newPassword) { success, message ->
                    if (success) {
                        onComplete(true, "Password changed successfully.")
                    }
                    else {
                        onComplete(false, message)
                    }
                    Log.d("Msg", message)
                }
            }
            else {
                onComplete(false, "Old password is incorrect.")
            }
        }
    }

    fun changeUsername(newUsername : String, onComplete: (Boolean, String) -> Unit) {
        if(newUsername.isEmpty()) {
            onComplete(false, "New username cannot be empty.")
            return
        }
        userRepository.changeUsername(newUsername) { success, message ->
            if(success) {
                onComplete(true, "Username changed successfully.")
            }
            else {
                onComplete(false, message)
            }
        }
    }

    fun changeProfileImage(imageUri: Uri, onComplete: (Boolean, String) -> Unit) {
        userRepository.changeProfileImage(imageUri) { success, message ->
            if (success) {
                onComplete(true, "Profile image updated successfully")
            } else {
                onComplete(false, message)
            }
        }
    }

    fun getApprovedShelters(onComplete: (List<User>?, Exception?) -> Unit) {
        userRepository.getApprovedShelters(onComplete)
    }
}