package edu.bluejack23_1.petia.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.Message
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.viewmodel.AuthViewModel

class MessengerRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getAllConversations(userID: String?, viewModel: AuthViewModel, callback: (ArrayList<Messenger>) -> Unit) {
        viewModel.getUserByID(userID) { u ->
            Log.d("tes", ""+u?.role.toString())
            if(u?.role.toString() == "Shelter") {
                val query = db.collection("messenger").whereEqualTo("shelterID", userID)

                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        val newMessages = ArrayList<Messenger>()
                        for (document in querySnapshot.documents) {
                            val message = document.toObject(Messenger::class.java)
                            if (message != null) {
                                message.documentID = document.id
                                newMessages.add(message)
                            }
                        }
                        callback(newMessages)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore Error", "Error getting comments", exception)
                    }
            }
            else {
                val query = db.collection("messenger").whereEqualTo("userID", userID)

                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        val newMessages = ArrayList<Messenger>()
                        for (document in querySnapshot.documents) {
                            val message = document.toObject(Messenger::class.java)
                            if (message != null) {
                                message.documentID = document.id
                                newMessages.add(message)
                            }
                        }
                        callback(newMessages)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore Error", "Error getting comments", exception)
                    }
            }
        }
    }

    fun createConversation(userID: String?, shelterID: String?) {
        if (shelterID == null || userID == null) {
            return
        }
        val initialMessage = Message(Timestamp.now(), "", "", "")

        val messengerData = hashMapOf(
            "userID" to userID,
            "shelterID" to shelterID,
            "latestMessage" to "",
            "lastUpdated" to Timestamp.now(),
            "messages" to listOf(initialMessage.toMap())
        )

        db.collection("messenger")
            .add(messengerData)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->

            }
    }

}