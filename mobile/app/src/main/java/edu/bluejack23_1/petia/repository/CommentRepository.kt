package edu.bluejack23_1.petia.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.petia.model.Comment

class CommentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getComment(commentID: String ?= null, callback: (Comment?) -> Unit) {
        db.collection("comments")
            .whereEqualTo(FieldPath.documentId(), commentID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val comment = document.toObject(Comment::class.java)
                    callback(comment)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting comment data", exception)
                callback(null)
            }
    }

    fun getAllComments(
        shelterID: String?,
        callback: (ArrayList<Comment>) -> Unit
    ) {
        val query = db.collection("comments")
            .whereEqualTo("shelterID", shelterID)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val newComments = ArrayList<Comment>()
                for (document in querySnapshot.documents) {
                    val comment = document.toObject(Comment::class.java)
                    if (comment != null) {
                        comment.documentID = document.id
                        newComments.add(comment)
                    }
                }
                callback(newComments)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting comments", exception)
            }
    }

    fun postComment(shelterID: String?, userID: String?, comment: String?) {
        if (shelterID == null || userID == null || comment == null) {
            return
        }

        val commentData = hashMapOf(
            "shelterID" to shelterID,
            "userID" to userID,
            "comment" to comment
        )

        db.collection("comments")
            .add(commentData)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->

            }
    }

    fun deleteComment(shelterID: String, userID: String, comment: String, callback: (Boolean) -> Unit) {
        val query = db.collection("comments")
            .whereEqualTo("shelterID", shelterID)
            .whereEqualTo("userID", userID)
            .whereEqualTo("comment", comment)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Comment deleted successfully")
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore Error", "Error deleting comment", e)
                            callback(false)
                        }
                } else {
                    Log.d("Firestore", "Comment not found")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore Error", "Error querying comments", e)
                callback(false)
            }
    }
}