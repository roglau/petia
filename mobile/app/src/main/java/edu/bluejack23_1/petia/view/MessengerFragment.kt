package edu.bluejack23_1.petia.view

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.databinding.FragmentMessengerBinding
import edu.bluejack23_1.petia.model.Comment
import edu.bluejack23_1.petia.model.Message
import edu.bluejack23_1.petia.model.Messenger
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.util.MessengerAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.MessengerViewModel

class MessengerFragment : Fragment() {
    private lateinit var binding : FragmentMessengerBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var messengerViewModel : MessengerViewModel
    private lateinit var messengerAdapter: MessengerAdapter
    private lateinit var messengerArrayList: ArrayList<Messenger>
    private var selectedShelterId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessengerBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        messengerViewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)

        recyclerView = binding.messengerRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        messengerArrayList = arrayListOf()
        messengerAdapter = MessengerAdapter(messengerArrayList, messengerViewModel, viewModel, requireContext())
        recyclerView.adapter = messengerAdapter

        messengerViewModel.messengerLiveData.observe(viewLifecycleOwner) { m ->
            updateRecyclerView(m)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val userID = sharedPreferencesManager.getUserId()

        messengerViewModel.messengerLiveData.observe(viewLifecycleOwner) { m ->
            updateRecyclerView(m)
        }
        messengerViewModel.getAllConversations(userID, viewModel)

        viewModel.getUserByID(userID) { user ->
            if(user?.role.toString() == "Shelter") {
                binding.newMessengerBtn.visibility = View.GONE
            }
            else {
                binding.newMessengerBtn.visibility = View.VISIBLE
            }
        }

        binding.newMessengerBtn.setOnClickListener {
            showDialog()
        }
    }

    private fun updateRecyclerView(messenger: ArrayList<Messenger>) {
        messengerAdapter.updateData(messenger)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(edu.bluejack23_1.petia.R.layout.fragment_create_conversation)

        val closeBtn = dialog.findViewById<Button>(edu.bluejack23_1.petia.R.id.closeCreateConversation)
        val createBtn = dialog.findViewById<Button>(edu.bluejack23_1.petia.R.id.create_conversation_btn)

        var shelters: List<User>? = null

        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val userID = sharedPreferencesManager.getUserId()

        val autoCompleteTextView = dialog.findViewById<TextInputLayout>(edu.bluejack23_1.petia.R.id.shelterDD)
        val shelterDD = autoCompleteTextView.editText as? AutoCompleteTextView

        viewModel.getApprovedShelters { sheltersList, exception ->
            if (exception != null) {

            } else {
                val approvedShelters = messengerViewModel.messengerLiveData.value
                val shelterIdsInConversations = approvedShelters?.map { it.shelterID } ?: emptyList()

                shelters = sheltersList?.filter { shelter ->
                    val shelterID = shelter.id
                    !shelterIdsInConversations.contains(shelterID)
                }

                val shelterNames = shelters?.map { it.username }?: emptyList()
                val shelterIds = shelters?.map { it.id }?: emptyList()

                val shelterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, shelterNames)
                shelterDD?.setAdapter(shelterAdapter)

                shelterDD?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedShelterId = shelterIds.get(position)
                }
            }
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        createBtn.setOnClickListener {
            if (selectedShelterId != null) {
                messengerViewModel.createConversation(userID, selectedShelterId)
                messengerViewModel.messengerLiveData.observe(viewLifecycleOwner) { m ->
                    updateRecyclerView(m)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = edu.bluejack23_1.petia.R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}