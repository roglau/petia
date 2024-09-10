package edu.bluejack23_1.petia.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Comment
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.view.ChatActivity
import edu.bluejack23_1.petia.view.PetDetailActivity
import edu.bluejack23_1.petia.view.ProfileFragment
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.CommentViewModel
import edu.bluejack23_1.petia.viewmodel.MessengerViewModel
import java.text.SimpleDateFormat

class MessengerAdapter(private val messengerList: ArrayList<Messenger>, private val viewModel: MessengerViewModel, private val userViewModel: AuthViewModel, private val context: Context) : RecyclerView.Adapter<MessengerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessengerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_list, parent, false)
        return MessengerAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessengerAdapter.MyViewHolder, position: Int) {
        val messenger : Messenger = messengerList[position]
        val sharedPreferencesManager = SharedPreferencesManager(context)

        val userID = sharedPreferencesManager.getUserId()

        if(userID == messenger.userID) {
            userViewModel.getUserByID(messenger.shelterID) { user ->
                holder.shelterName.text = user?.username
                Picasso.get().load(user?.profileImage).into(holder.profileImage)
            }
        }
        else if(userID == messenger.shelterID) {
            userViewModel.getUserByID(messenger.userID) { user ->
                holder.shelterName.text = user?.username
                Picasso.get().load(user?.profileImage).into(holder.profileImage)
            }
        }
        holder.messageText.text = messenger.latestMessage
        holder.lastUpdated.text = formatTimestamp(messenger.lastUpdated ?: Timestamp.now())

        holder.conversationCardView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                val messenger = messengerList[position]

                sharedPreferencesManager.setChosenConversationID(messenger.documentID.toString())
                sharedPreferencesManager.setChosenShelterID(messenger.shelterID.toString())

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("messengerID", messenger.documentID)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return messengerList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val conversationCardView : CardView = itemView.findViewById(R.id.conversationCardView)
        val profileImage : ImageView = itemView.findViewById(R.id.messengerProfileImage)
        val shelterName : TextView = itemView.findViewById(R.id.messengerShelterName)
        val messageText : TextView = itemView.findViewById(R.id.messengerText)
        val lastUpdated : TextView = itemView.findViewById(R.id.messengerLastUpdated)
    }

    fun updateData(newData: ArrayList<Messenger>) {
        val uniqueData = ArrayList<Messenger>()

        for (item in newData) {
            if (!messengerList.contains(item)) {
                uniqueData.add(item)
            }
        }
        messengerList.addAll(uniqueData)
        notifyDataSetChanged()
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("MMM dd")
        return sdf.format(date)
    }

    public fun updateConversations(messenger: Messenger) {
        messengerList.add(messenger)
        notifyDataSetChanged()
    }
}