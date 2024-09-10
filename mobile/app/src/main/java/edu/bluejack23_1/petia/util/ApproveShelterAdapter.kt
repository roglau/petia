import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.model.User
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.UserViewModel

class ApproveShelterAdapter(private val viewModel: UserViewModel) : RecyclerView.Adapter<ApproveShelterAdapter.ViewHolder>() {

    private var shelterRequests: ArrayList<User> = arrayListOf()

    fun updateData(newData: ArrayList<User>) {
        shelterRequests = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profile)
        val contentTextView: TextView = itemView.findViewById(R.id.content)
        val requestDateTextView: TextView = itemView.findViewById(R.id.requestDate)
        val approveButton: Button = itemView.findViewById(R.id.approveBtn)
        val cancelButton: Button = itemView.findViewById(R.id.cancelBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.approve_shelter_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shelterRequest = shelterRequests[position]

        println(shelterRequest)


        Picasso.get().load(shelterRequest.profileImage).into(holder.profileImageView)
        holder.contentTextView.text = shelterRequest.username + " has applied for shelter role"
        holder.requestDateTextView.text = "1 hour ago"

        holder.approveButton.setOnClickListener {
            viewModel.approveUser(shelterRequest){ callback ->
                if(callback){
                    removeItem(position)
                }
            }

        }

        holder.cancelButton.setOnClickListener {
            viewModel.removeUser(shelterRequest){ callback ->
                if(callback){
                    removeItem(position)
                }
            }
        }
    }

    private fun removeItem(position: Int) {
        shelterRequests.removeAt(position)
        notifyItemRemoved(position)
        updatePositionsAfterRemoval(position)
    }

    private fun updatePositionsAfterRemoval(startingPosition: Int) {
        for (i in startingPosition until shelterRequests.size) {
            notifyItemChanged(i)
        }
    }

    override fun getItemCount(): Int {
        return shelterRequests.size
    }
}
