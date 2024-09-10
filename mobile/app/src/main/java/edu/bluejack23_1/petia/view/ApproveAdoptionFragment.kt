package edu.bluejack23_1.petia.view

import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel
import ApproveAdoptionAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.petia.databinding.FragmentApproveAdoptionBinding
import edu.bluejack23_1.petia.util.SharedPreferencesManager


class ApproveAdoptionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: ApproveAdoptionAdapter
    private lateinit var viewModel: AdoptionViewModel
    private lateinit var binding : FragmentApproveAdoptionBinding
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
        binding = FragmentApproveAdoptionBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        myAdapter = ApproveAdoptionAdapter(viewModel)
        recyclerView.adapter = myAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = sharedPreferencesManager.getUserId()
        id?.let { viewModel.getAllRequests(it) }

        viewModel.userRequests.observe(viewLifecycleOwner) { userRequests ->
            myAdapter.updateData(userRequests)
        }

    }

}