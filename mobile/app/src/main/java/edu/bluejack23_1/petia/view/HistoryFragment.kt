package edu.bluejack23_1.petia.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentApproveAdoptionBinding
import edu.bluejack23_1.petia.databinding.FragmentHistoryBinding
import edu.bluejack23_1.petia.util.AdoptionHistoryAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: AdoptionHistoryAdapter
    private lateinit var viewModel: AdoptionViewModel
    private lateinit var binding : FragmentHistoryBinding
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        var someActivityResultLauncher: ActivityResultLauncher<Intent>

        someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                changeFragment(ProfileFragment())
            }
        }

        myAdapter = AdoptionHistoryAdapter(viewModel, requireContext(), sharedPreferencesManager, someActivityResultLauncher)
        recyclerView.adapter = myAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = sharedPreferencesManager.getUserId()
        id?.let { viewModel.fetchAllUserHistories(it) }

        viewModel.userHistories.observe(viewLifecycleOwner) { userHistories ->
            myAdapter.updateData(userHistories)
        }

    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}