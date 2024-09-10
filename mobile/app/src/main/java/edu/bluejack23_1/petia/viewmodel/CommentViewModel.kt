package edu.bluejack23_1.petia.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.Comment
import edu.bluejack23_1.petia.repository.CommentRepository

class CommentViewModel : ViewModel() {
    private val commentRepository = CommentRepository()
    val commentLiveData = MutableLiveData<ArrayList<Comment>>()

    fun getComment(commentID: String ?= null, callback: (Comment?) -> Unit) {
        commentRepository.getComment(commentID, callback)
    }

    fun loadComments(shelterID: String?) {
        commentRepository.getAllComments(shelterID) { comments ->
            commentLiveData.postValue(comments)
        }
    }

    fun postComment(shelterID: String?, userID: String?, comment: String?) {
        commentRepository.postComment(shelterID, userID, comment)
    }

    fun deleteComment(shelterID: String, userID: String, comment: String, context: Context) {
        commentRepository.deleteComment(shelterID, userID, comment) { success ->
            if (success) {
                showToast("Comment deleted successfully", context)
            }
            else {

            }
        }
    }

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}