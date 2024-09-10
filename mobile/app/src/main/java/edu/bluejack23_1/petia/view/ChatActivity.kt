package edu.bluejack23_1.petia.view

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.ActivityChatBinding
import edu.bluejack23_1.petia.model.Message
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.model.Pet
import edu.bluejack23_1.petia.util.MessageAdapter
import edu.bluejack23_1.petia.util.MessengerAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.MessageViewModel
import edu.bluejack23_1.petia.viewmodel.MessengerViewModel

class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageViewModel : MessageViewModel
    private lateinit var messengerAdapter: MessengerAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageArrayList: ArrayList<Message>
    private lateinit var messengerViewModel : MessengerViewModel
    private lateinit var messengerArrayList: ArrayList<Messenger>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        messengerViewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.hasFixedSize()

        messageArrayList = arrayListOf()
        messengerArrayList = arrayListOf()
        messageAdapter = MessageAdapter(messageArrayList, messageViewModel, viewModel, applicationContext)
        messengerAdapter = MessengerAdapter(messengerArrayList, messengerViewModel, viewModel, applicationContext)
        recyclerView.adapter = messageAdapter

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val sendBtn = findViewById<ImageButton>(R.id.sendMessage)
        val messageInput = findViewById<EditText>(R.id.messageInput)

        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        val userID = sharedPreferencesManager.getUserId()
        val shelterID = sharedPreferencesManager.getChosenShelterID()
        val messengerID = sharedPreferencesManager.getChosenConversationID()

        messageViewModel.getConversationMessages(messengerID)

        viewModel.getUserByID(shelterID) { user ->
            val shelterName = findViewById<TextView>(R.id.shelterName)
            val shelterProfile = findViewById<ImageView>(R.id.shelterProfile)
            shelterName.text = user?.username
            Picasso.get().load(user?.profileImage).into(shelterProfile)
        }

        messageViewModel.messageLiveData.observe(this) { m ->
            Log.d("tes", "" + m)
            updateRecyclerView(m)
        }

        sendBtn.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotBlank()) {
                messageViewModel.insertMessage(messengerID, messageText, userID.toString(), shelterID.toString())
                messageInput.text.clear()
            }
        }

        eventChangeListener(messengerID.toString())
    }

    private fun updateRecyclerView(messages: List<Message>) {
        messageAdapter.updateData(messages)
    }

    private fun eventChangeListener(messengerID: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("messenger")
            .document(messengerID)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val messagesArray = snapshot["messages"] as? ArrayList<HashMap<String, Any>>
                    val newMessages = ArrayList<Message>()

                    messagesArray?.let {
                        for (messageMap in it) {
                            val message = Message(
                                messageMap["createdAt"] as Timestamp,
                                messageMap["from"] as String,
                                messageMap["text"] as String,
                                messageMap["to"] as String
                            )
                            newMessages.add(message)
                        }
                    }
                    messageViewModel.messageLiveData.value = newMessages
                }
            }
    }
}