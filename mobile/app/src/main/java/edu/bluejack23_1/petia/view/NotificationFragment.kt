package edu.bluejack23_1.petia.view

import ApproveAdoptionAdapter
import ApproveShelterAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentApproveAdoptionBinding
import edu.bluejack23_1.petia.databinding.FragmentApproveShelterBinding
import edu.bluejack23_1.petia.databinding.FragmentNotificationBinding
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel
import edu.bluejack23_1.petia.viewmodel.UserViewModel


class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shelterAdapter: ApproveAdoptionAdapter
    private lateinit var adminAdapter: ApproveShelterAdapter
    private lateinit var adoptionViewModel: AdoptionViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding : FragmentNotificationBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        sharedPreferencesManager = SharedPreferencesManager(context)
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        adoptionViewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        adminAdapter = ApproveShelterAdapter(UserViewModel())
        shelterAdapter = ApproveAdoptionAdapter(AdoptionViewModel())

        if(sharedPreferencesManager.getUserRole() == "Admin"){
            recyclerView.adapter =adminAdapter
        }else if(sharedPreferencesManager.getUserRole() == "Shelter"){
            recyclerView.adapter = shelterAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(sharedPreferencesManager.getUserRole() == "Admin"){
            userViewModel.getAllUnapprovedUsers()

            userViewModel.unapprovedShelter.observe(viewLifecycleOwner) { unapprovedUsersList ->
                adminAdapter.updateData(unapprovedUsersList)
            }
        }else if(sharedPreferencesManager.getUserRole() == "Shelter"){
            var id = sharedPreferencesManager.getUserId()
            id?.let { adoptionViewModel.getAllRequests(it) }

            adoptionViewModel.userRequests.observe(viewLifecycleOwner) { userRequests ->
                shelterAdapter.updateData(userRequests)
            }
        }

    }

}