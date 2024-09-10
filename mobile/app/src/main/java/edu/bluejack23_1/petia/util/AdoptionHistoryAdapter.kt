package edu.bluejack23_1.petia.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.AdoptionHistory
import edu.bluejack23_1.petia.model.AdoptionRequest
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.util.DateUtils
import edu.bluejack23_1.petia.view.CreateAdoptionActivity
import edu.bluejack23_1.petia.view.HistoryDetailActivity
import edu.bluejack23_1.petia.view.PetDetailActivity
import edu.bluejack23_1.petia.view.ProfileFragment
import edu.bluejack23_1.petia.viewmodel.AdoptionViewModel
import java.util.Date

class AdoptionHistoryAdapter(private val viewModel: AdoptionViewModel, private val context: Context
, private val sharedPreferencesManager: SharedPreferencesManager
, private val someActivityResultLauncher: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<AdoptionHistoryAdapter.ViewHolder>() {

    private var adoptionRequests: ArrayList<AdoptionHistory> = arrayListOf()

    fun updateData(newData: ArrayList<AdoptionHistory>) {
        adoptionRequests = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card : LinearLayout = itemView.findViewById(R.id.historyCard)
        val profileImageView: ImageView = itemView.findViewById(R.id.profile)
        val contentTextView: TextView = itemView.findViewById(R.id.content)
        val breedTextView: TextView = itemView.findViewById(R.id.breed)
        val adoptDateTextView: TextView = itemView.findViewById(R.id.adoptDate)
        val adoptStatusTextView: TextView = itemView.findViewById(R.id.adoptStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shelterRequest = adoptionRequests[position]

        Picasso.get().load(shelterRequest.pet?.petImage).into(holder.profileImageView)
        holder.contentTextView.text = shelterRequest.pet?.petName + " - " + shelterRequest.shelter?.username
        holder.breedTextView.text = shelterRequest.pet?.petBreed
        holder.adoptDateTextView.text = shelterRequest.adoption?.date?.let {
            DateUtils.formatDateAsDayMonthYear(
                it
            )
        }
        val statusText = shelterRequest.adoption?.status
        holder.adoptStatusTextView.text = statusText

        when (statusText) {
            "Pending" -> holder.adoptStatusTextView.setTextColor(ContextCompat.getColor(context, R.color.dark_yellow))
            "Rejected" -> holder.adoptStatusTextView.setTextColor(Color.RED)
            "Completed" -> holder.adoptStatusTextView.setTextColor(Color.GREEN)
            else -> holder.adoptStatusTextView.setTextColor(Color.BLACK)
        }

        holder.contentTextView.setOnClickListener{
            shelterRequest.shelter?.id?.let { it1 -> sharedPreferencesManager.setChosenUserId(it1) }
            changeFragment(ProfileFragment())
        }


        holder.card.setOnClickListener{
            if (position != RecyclerView.NO_POSITION) {
                val history = adoptionRequests[position]

                val intent = Intent(context, HistoryDetailActivity::class.java)
                intent.putExtra("adoptionId", history.adoption?.id)
                someActivityResultLauncher.launch(intent)

//                val intent = Intent(context, HistoryDetailActivity::class.java)
//                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return adoptionRequests.size
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}
