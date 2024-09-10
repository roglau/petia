package edu.bluejack23_1.petia.view

import ApproveShelterAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.rpc.context.AttributeContext.Auth
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentApproveShelterBinding
import edu.bluejack23_1.petia.util.PetNWishlistAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.UserViewModel

class ApproveShelterFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: ApproveShelterAdapter
    private lateinit var viewModel: UserViewModel
    private lateinit var binding : FragmentApproveShelterBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val context = requireContext()
        sharedPreferencesManager = SharedPreferencesManager(context)
        binding = FragmentApproveShelterBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        myAdapter = ApproveShelterAdapter(viewModel)
        recyclerView.adapter = myAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllUnapprovedUsers()

        viewModel.unapprovedShelter.observe(viewLifecycleOwner) { unapprovedUsersList ->
            myAdapter.updateData(unapprovedUsersList)
        }

    }

}