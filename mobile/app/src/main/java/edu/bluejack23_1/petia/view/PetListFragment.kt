package edu.bluejack23_1.petia.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentPetListBinding
import edu.bluejack23_1.petia.model.Filter
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.util.PetNWishlistAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.PetViewModel
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel


class PetListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: PetNWishlistAdapter
    private lateinit var viewModel: WishlistViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var binding : FragmentPetListBinding
    private var filter: Filter? = null
    private var search: String? = null
    private val limitPerPage = 3;
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
        binding = FragmentPetListBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)
        petViewModel = ViewModelProvider(this).get(PetViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.hasFixedSize()

        var someActivityResultLauncher: ActivityResultLauncher<Intent>

        someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val messenger = data?.getIntExtra("messenger", 0)

                if (messenger == 1) {
                    changeFragment(MessengerFragment())
                }else{
                    changeFragment(ProfileFragment())
                }

            }
        }

        myAdapter = PetNWishlistAdapter(viewModel, context,someActivityResultLauncher)
        recyclerView.adapter = myAdapter


        return binding.root
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val searchValue = sharedPreferencesManager.getSearch()

        binding.searchET.setText(searchValue)

        binding.searchET.doOnTextChanged { text, start, before, count ->
            search = text.toString();

            filter = Filter(
                animal = filter?.animal,
                size = filter?.size,
                age = filter?.age,
                color = filter?.color,
                breed = filter?.breed,
                type = search
            )
            petViewModel.updateFilter(filter)

        }

        filter = Filter(
            animal = null,
            size = null,
            age = null,
            color = null,
            breed = null,
            type = search
        )

        binding.filterET.setOnClickListener {
            showDialog()
        }

        petViewModel.filterLiveData.observe(viewLifecycleOwner) { updatedFilter ->
            println("Filter $updatedFilter")
            myAdapter.resetData()
            filter =  updatedFilter
            petViewModel.loadData(limitPerPage, sharedPreferencesManager.getUserId(), updatedFilter)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                print("oioioi")
                println(layoutManager)
                println(visibleItemCount)
                println(totalItemCount)
                println(firstVisibleItemPosition)

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadMoreData()
                }
            }
        })

        petViewModel.petNWishlistLiveData.observe(viewLifecycleOwner) { petNWishlist ->
            updateRecyclerView(petNWishlist)
        }

        petViewModel.loadData(limitPerPage, sharedPreferencesManager.getUserId(), filter)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_filter_dialog)

        val closeBtn = dialog.findViewById<Button>(R.id.btnCloseDialog)
        val applyBtn = dialog.findViewById<Button>(R.id.applyBtn)

        val animal = arrayOf("Dog", "Cat")
        val size = arrayOf("Small", "Medium", "Large")
        val age = arrayOf("Young", "Medium", "Old")

        val animalDD = dialog.findViewById<TextInputLayout>(R.id.animalDD)
        val sizeDD = dialog.findViewById<TextInputLayout>(R.id.sizeDD)
        val ageDD = dialog.findViewById<TextInputLayout>(R.id.ageDD)

        val animalAdapter = ArrayAdapter(requireContext(), R.layout.list_item, animal)
        val sizeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, size)
        val ageAdapter = ArrayAdapter(requireContext(), R.layout.list_item, age)

        (animalDD.editText as? AutoCompleteTextView)?.setAdapter(animalAdapter)
        (sizeDD.editText as? AutoCompleteTextView)?.setAdapter(sizeAdapter)
        (ageDD.editText as? AutoCompleteTextView)?.setAdapter(ageAdapter)

        val breedTF = dialog.findViewById<TextInputLayout>(R.id.breedDD)
        val colorTF = dialog.findViewById<TextInputLayout>(R.id.colorDD)


        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        applyBtn.setOnClickListener{
            filter = Filter(
                animal = animalDD.editText?.text.toString(),
                size = sizeDD.editText?.text.toString(),
                age = ageDD.editText?.text.toString(),
                color = colorTF.editText?.text.toString(),
                breed = breedTF.editText?.text.toString(),
                type = search
            )

            petViewModel.updateFilter(filter)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }


    private fun updateRecyclerView(petNWishlist: ArrayList<PetNWishlist>) {
        // Update the adapter with the new data
        myAdapter.updateData(petNWishlist)
    }

    private fun loadMoreData() {
        petViewModel.loadData(myAdapter.itemCount + limitPerPage, sharedPreferencesManager.getUserId(), filter)
    }
}