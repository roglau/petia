package edu.bluejack23_1.petia.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.model.PetNWishlist
import edu.bluejack23_1.petia.model.Wishlist
import edu.bluejack23_1.petia.view.MainActivity
import edu.bluejack23_1.petia.view.PetDetailActivity
import edu.bluejack23_1.petia.viewmodel.WishlistViewModel


class PetNWishlistAdapter(private val viewModel: WishlistViewModel, private val context: Context
,private val someActivityResultLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<PetNWishlistAdapter.MyViewHolder>() {

    private var petList: ArrayList<PetNWishlist> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_pet_list, parent, false)

        return MyViewHolder(itemView)
    }

    fun resetData(){
        petList = arrayListOf()
    }

    fun updateData(newData: ArrayList<PetNWishlist>) {
//        println("newData $newData")
//        println("petlist $petList")
        val uniqueData = ArrayList<PetNWishlist>()

        for (item in newData) {
            if (!petList.contains(item)) {
                uniqueData.add(item)
            }
        }

        petList.addAll(uniqueData)
        notifyDataSetChanged()
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pet : PetNWishlist = petList[position]
        holder.petName.text = pet.petName

        val petImage = pet.petImage
        if (petImage != null) {
            Picasso.get().load(petImage).into(holder.petImage)
        }

        holder.cardView.setOnClickListener{
            if (position != RecyclerView.NO_POSITION) {
                val pet = petList[position]
    
                val intent = Intent(context, PetDetailActivity::class.java)
                intent.putExtra("petID", pet.documentID)
                someActivityResultLauncher.launch(intent)
            }
        }

        if(pet.liked == true){
            val colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
            holder.wishlistBtn.colorFilter = colorFilter
        }else{
            val colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            holder.wishlistBtn.colorFilter = colorFilter
        }

        val petID = pet.documentID
        val sharedPreferencesManager = SharedPreferencesManager(context)
        val userID = sharedPreferencesManager.getUserId()

        holder.wishlistBtn.setOnClickListener {
            val clickedPet = petList[position]
            val wishlist = Wishlist(userID, clickedPet.documentID)
            if(clickedPet.liked == true){
                viewModel.deleteWishlist(petID, userID) { success, message ->
                    if (success) {
                        petList[position].liked = false
                        notifyItemChanged(position)
                    }
                }
            }else{
                viewModel.addToWishlist(wishlist) { success, message ->
                    if (success) {
                        petList[position].liked = true
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petName : TextView = itemView.findViewById(R.id.homePetName)
        val petImage : ImageView = itemView.findViewById(R.id.homePetImage)
        val wishlistBtn : ImageButton = itemView.findViewById(R.id.wishlistButton)
        val cardView : RelativeLayout = itemView.findViewById(R.id.cardCon)


    }

}