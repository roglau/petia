package edu.bluejack23_1.petia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.Message
import edu.bluejack23_1.petia.repository.MessageRepository

class MessageViewModel : ViewModel() {
    private val messageRepository = MessageRepository()
    val messageLiveData = MutableLiveData<List<Message>>()

    fun getConversationMessages(messengerID: String?) {
        if (messengerID == null) {
            return
        }
        messageRepository.getConversationMessages(messengerID) { messages ->
            messageLiveData.postValue(messages)
        }
    }

    fun insertMessage(messengerID: String?, text: String, userID: String, shelterID: String) {
        if (messengerID == null) {
            return
        }
        messageRepository.insertMessage(messengerID, text, userID, shelterID)
    }
}