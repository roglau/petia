package edu.bluejack23_1.petia.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_LOGGED_IN = "userLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_PET_ID = "petId"
        private const val KEY_USER_ROLE = "userRole"
        private const val KEY_SEARCH = "search"
        private const val KEY_CHOSEN_USER_ID = "chosenUserId"
        private const val KEY_CHOSEN_CONVERSATION_ID = "chosenConversationID"
        private const val KEY_CHOSEN_SHELTER_ID = "chosenShelterID"
    }

    fun setSearch(searchValue: String) {
        sharedPreferences.edit().putString(KEY_SEARCH, searchValue).apply()
    }

    fun getSearch(): String? {
        return sharedPreferences.getString(KEY_SEARCH, null)
    }

    fun setUserRole(userRole : String) {
        sharedPreferences.edit().putString(KEY_USER_ROLE, userRole).apply()
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_USER_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false)
    }

    fun setUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun setPetId(petId: String) {
        sharedPreferences.edit().putString(KEY_PET_ID, petId).apply()
    }

    fun getPetId(): String? {
        return sharedPreferences.getString(KEY_PET_ID, null)
    }

    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    fun setChosenUserId(chosenUserId: String) {
        sharedPreferences.edit().putString(KEY_CHOSEN_USER_ID, chosenUserId).apply()
    }

    fun getChosenUserId(): String? {
        return sharedPreferences.getString(KEY_CHOSEN_USER_ID, null)
    }

    fun setChosenConversationID(chosenConversationID: String) {
        sharedPreferences.edit().putString(KEY_CHOSEN_CONVERSATION_ID, chosenConversationID).apply()
    }

    fun getChosenConversationID(): String? {
        return sharedPreferences.getString(KEY_CHOSEN_CONVERSATION_ID, null)
    }

    fun setChosenShelterID(chosenShelterID: String) {
        sharedPreferences.edit().putString(KEY_CHOSEN_SHELTER_ID, chosenShelterID).apply()
    }

    fun getChosenShelterID(): String? {
        return sharedPreferences.getString(KEY_CHOSEN_SHELTER_ID, null)
    }
}