package edu.bluejack23_1.petia.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.Message

class MessageRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getConversationMessages(messengerID: String?, callback: (List<Message>) -> Unit) {
        if (messengerID == null) {
            callback(emptyList())
            return
        }
        val messagesCollection = db.collection("messenger").document(messengerID).collection("messages")

        messagesCollection.orderBy("createdAt").get()
            .addOnSuccessListener { querySnapshot ->
                val messages = mutableListOf<Message>()

                for (document in querySnapshot.documents) {
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        Log.d("meseg array", ""+message)
                        messages.add(message)
                    }
                }
                callback(messages)
            }
            .addOnFailureListener { exception ->
                callback(emptyList())
            }
    }

    fun insertMessage(messengerID: String?, text: String, from: String, to: String) {
        if (messengerID == null) {
            return
        }

        val newMessage = hashMapOf(
            "createdAt" to Timestamp.now(),
            "from" to from,
            "text" to text,
            "to" to to
        )

        val messengerDocRef = db.collection("messenger").document(messengerID)

        val updateData = hashMapOf(
            "lastUpdated" to Timestamp.now(),
            "latestMessage" to text,
            "messages" to FieldValue.arrayUnion(newMessage)
        )

        messengerDocRef.update(updateData)
            .addOnSuccessListener {
                Log.d("MessageRepository", "Message added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MessageRepository", "Error adding message", e)
            }
    }
}