package edu.bluejack23_1.petia.util

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.Message
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.MessageViewModel
import edu.bluejack23_1.petia.viewmodel.MessengerViewModel
import java.text.SimpleDateFormat

class MessageAdapter(private val messageList: ArrayList<Message>, private val viewModel: MessageViewModel, private val userViewModel: AuthViewModel, private val context: Context) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_list, parent, false)
        return MessageAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageAdapter.MyViewHolder, position: Int) {
        val message : Message = messageList[position]
        val sharedPreferencesManager = SharedPreferencesManager(context)
        val userID = sharedPreferencesManager.getUserId()

        if(message.from == userID) {
            if(message.text != "") {
                holder.userCardView.visibility = View.VISIBLE
                holder.userDate.visibility = View.VISIBLE
                holder.userChat.text = message.text
                Log.d("asd", message.text)
                holder.userDate.text = formatTimestamp(message.createdAt)
            }
        }
        else {
            if(message.text != "") {
                holder.otherUserCardView.visibility = View.VISIBLE
                holder.otherUserDate.visibility = View.VISIBLE
                holder.otherUserChat.text = message.text
                holder.otherUserDate.text = formatTimestamp(message.createdAt)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val otherUserCardView : CardView = itemView.findViewById(R.id.shelterCardView)
        val otherUserChat : TextView = itemView.findViewById(R.id.shelterChat)
        val otherUserDate : TextView = itemView.findViewById(R.id.shelterDate)
        val userCardView : CardView = itemView.findViewById(R.id.userCardView)
        val userChat : TextView = itemView.findViewById(R.id.userChat)
        val userDate : TextView = itemView.findViewById(R.id.userDate)
    }

    fun updateData(newData: List<Message>) {
        val uniqueData = ArrayList<Message>()

        for (item in newData) {
            if (!messageList.contains(item)) {
                uniqueData.add(item)
            }
        }
        messageList.addAll(uniqueData)
        notifyDataSetChanged()
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("MMM dd")
        return sdf.format(date)
    }
}