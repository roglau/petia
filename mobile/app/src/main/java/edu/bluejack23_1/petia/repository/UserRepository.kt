package edu.bluejack23_1.petia.repository

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack23_1.petia.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("users")

    fun isEmailRegistered(email: String, onComplete: (Boolean) -> Unit) {
        val usersRef = db.collection("users")

        usersRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(!documents.isEmpty)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun recoverEmail(email: String, onComplete: (Boolean, String) -> Unit){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   onComplete(true, "Password reset email sent to $email")
                } else {
                    // Failed to send password reset email
                    onComplete(false, "Failed to send password reset email: ${task.exception?.message}")
                }
            }
    }

    fun getUserByEmail(email: String, onComplete: (User?) -> Unit) {
        val usersRef = db.collection("users")

        usersRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents[0]
                    val userData = userDocument.toObject(User::class.java)

                    userData?.id = userDocument.id
                    onComplete(userData)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                onComplete(null)
            }
    }

    fun getUserByID(userID: String? = null, onComplete: (User?) -> Unit) {
        if (userID != null) {
            val userDocumentRef = db.collection("users").document(userID)

            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(User::class.java)
                        onComplete(userData)
                    } else {
                        onComplete(null)
                    }
                }
                .addOnFailureListener { exception ->
                    onComplete(null)
                }
        } else {
            onComplete(null)
        }
    }

    fun registerUser(userData: User, onComplete: (Boolean, String) -> Unit) {
        if (userData.role == "Shelter") {
            val userDataMap = mapOf(
                "fullname" to userData.fullname,
                "username" to userData.username,
                "email" to userData.email,
                "password" to userData.password,
                "phone" to userData.phone,
                "role" to userData.role,
                "status" to userData.status,
                "profileImage" to userData.profileImage
            )

            db.collection("users")
                .add(userDataMap)
                .addOnSuccessListener {
                    onComplete(true, "Registration Successful!")
                }
                .addOnFailureListener {
                    onComplete(false, "Error saving user data to Firestore!")
                }
        } else {
            // User is not a shelter, proceed with Firebase Authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Save user data to Firestore
                        val userDocument = db.collection("users").document(user?.uid ?: "defaultUid")

                        val userDataMap = mapOf(
                            "fullname" to userData.fullname,
                            "username" to userData.username,
                            "email" to userData.email,
                            "phone" to userData.phone,
                            "role" to userData.role,
                            "status" to userData.status,
                            "profileImage" to userData.profileImage
                        )

                        userDocument.set(userDataMap)
                            .addOnSuccessListener {
                                onComplete(true, "Registration Successful!")
                            }
                            .addOnFailureListener {
                                onComplete(false, "Error saving user data to Firestore!")
                            }
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        onComplete(false, errorMessage)
                    }
                }
        }
    }


    fun loginUser(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Login successful")
                } else {
                    val errorMessage = task.exception?.message ?: "Login failed"
                    onComplete(false, errorMessage)
                }
            }
    }

    fun logoutUser(onComplete: (Boolean) -> Unit) {
        auth.signOut()
        onComplete(true)
    }

    fun getAllUnapprovedUsers(callback: (List<User>?, Exception?) -> Unit) {
        usersCollection.whereEqualTo("status", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val unapprovedUsers = querySnapshot.toObjects(User::class.java)
                callback(unapprovedUsers, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }


    fun approveUser(userData: User, onComplete: (Boolean, String)  -> Unit) {
        userData.id?.let { userId ->
            // Delete user from Firestore
            usersCollection.document(userId)
                .delete()
                .addOnSuccessListener {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(userData.email, userData.password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser

                                // Save user data to Firestore
                                val userDocument = db.collection("users").document(user?.uid ?: "defaultUid")

                                val userDataMap = mapOf(
                                    "fullname" to userData.fullname,
                                    "username" to userData.username,
                                    "email" to userData.email,
                                    "phone" to userData.phone,
                                    "role" to userData.role,
                                    "status" to true,
                                    "profileImage" to userData.profileImage
                                )

                                userDocument.set(userDataMap)
                                    .addOnSuccessListener {
                                        onComplete(true, "Registration Successful!")
                                    }
                                    .addOnFailureListener {
                                        onComplete(false, "Error saving user data to Firestore!")
                                    }
                            } else {
                                val errorMessage = task.exception?.message ?: "Unknown error"
                                onComplete(false, errorMessage)
                            }
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle the failure to delete user from Firestore
                    onComplete(false, "") // Approval failed
                }
        }
    }


    fun removeUser(user: User, callback: (Boolean) -> Unit) {
        user.id?.let {
            usersCollection.document(it)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { exception ->
                    callback(false) // Removal failed
                }
        }
    }
    fun changePassword(oldPassword: String, newPassword: String, onComplete: (Boolean, String) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthResult ->
                if (reauthResult.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateResult ->
                        if (updateResult.isSuccessful) {
                            onComplete(true, "Password changed successfully")
                        } else {
                            val errorMessage = updateResult.exception?.message ?: "Failed to change password"
                            onComplete(false, errorMessage)
                        }
                    }
                } else {
                    val errorMessage = reauthResult.exception?.message ?: "Failed to reauthenticate"
                    onComplete(false, errorMessage)
                }
            }
        } else {
            onComplete(false, "User not authenticated")
        }
    }

    fun validateOldPassword(oldPassword: String, onValidationResult: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    onValidationResult(task.isSuccessful)
                }
        }
        else {
            onValidationResult(false)
        }
    }

    fun changeUsername(newUsername: String, onComplete: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDataMap = mapOf("username" to newUsername)

            db.collection("users").document(user.uid).update(userDataMap).addOnSuccessListener {
                    onComplete(true, "Username changed successfully")
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message ?: "Failed to change username"
                    onComplete(false, errorMessage)
                }
        }
        else {
            onComplete(false, "User not authenticated")
        }
    }

    fun changeProfileImage(selectedImageUri: Uri, onComplete: (Boolean, String) -> Unit) {
        val storageRef = storage.getReferenceFromUrl("gs://petia-9fa03.appspot.com/")
        val filename = "${System.currentTimeMillis()}_${selectedImageUri.lastPathSegment}"
        val imageRef = storageRef.child("$filename")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        val user = FirebaseAuth.getInstance().currentUser
                        val userDataMap = mapOf("profileImage" to imageUrl)

                        db.collection("users").document(user?.uid ?: "defaultUid")
                            .update(userDataMap)
                            .addOnSuccessListener {
                                onComplete(true, "Profile image changed successfully.")
                            }
                            .addOnFailureListener { firestoreException ->
                                val errorMessage = firestoreException.message ?: "Failed to update profile image in Firestore"
                                onComplete(false, errorMessage)
                            }
                    }
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Failed to upload profile image to Firebase Storage"
                onComplete(false, errorMessage)
            }
    }

    fun getApprovedShelters(callback: (List<User>?, Exception?) -> Unit) {
        usersCollection.whereEqualTo("role", "Shelter")
            .whereEqualTo("status", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val approvedShelters = querySnapshot.toObjects(User::class.java)
                callback(approvedShelters, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }
}