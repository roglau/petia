package edu.bluejack23_1.petia.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Comment
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.view.ProfileFragment
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.CommentViewModel

class CommentAdapter(private val commentList: ArrayList<Comment>, private val viewModel: CommentViewModel, private val userViewModel: AuthViewModel, private val context: Context) : RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shelter_comment_list, parent, false)
        return CommentAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentAdapter.MyViewHolder, position: Int) {
        val comment : Comment = commentList[position]
        holder.comment.text = comment.comment

        userViewModel.getUserByID(comment.userID) { user ->
            if (user != null) {
                holder.commenter.text = user.username
                Picasso.get().load(user.profileImage).into(holder.profileImage)

                val sharedPreferencesManager = SharedPreferencesManager(context)

                holder.profileImage.setOnClickListener {
                    sharedPreferencesManager.setChosenUserId(comment.userID.toString())
                    changeFragment(ProfileFragment())
                }

                if(comment.shelterID == sharedPreferencesManager.getUserId()) {
                    holder.deleteComment.visibility = View.VISIBLE;
                }

                holder.deleteComment.setOnClickListener {
                    viewModel.deleteComment(comment.shelterID.toString(), comment.userID.toString(), comment.comment.toString(), context)
                    if (position in 0 until commentList.size) {
                        commentList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage : ImageView = itemView.findViewById(R.id.commentProfile)
        val commenter : TextView = itemView.findViewById(R.id.commenterName)
        val comment : TextView = itemView.findViewById(R.id.comment)
        val deleteComment : Button = itemView.findViewById(R.id.deleteCommentBtn)
    }

    fun updateData(newData: ArrayList<Comment>) {
        val uniqueData = ArrayList<Comment>()

        for (item in newData) {
            if (!commentList.contains(item)) {
                uniqueData.add(item)
            }
        }
        commentList.addAll(uniqueData)
        notifyDataSetChanged()
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    public fun updateComments(comment: Comment) {
        commentList.add(comment)
        notifyDataSetChanged()
    }
}