package edu.bluejack23_1.petia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.repository.MessengerRepository

class MessengerViewModel : ViewModel() {
    private val messengerRepository = MessengerRepository()
    val messengerLiveData = MutableLiveData<ArrayList<Messenger>>()

    fun getAllConversations(userID: String?, viewModel: AuthViewModel) {
        messengerRepository.getAllConversations(userID, viewModel) { conversations ->
            messengerLiveData.postValue(conversations)
        }
    }

    fun createConversation(userID: String?, shelterID: String?) {
        messengerRepository.createConversation(userID, shelterID)
    }
}