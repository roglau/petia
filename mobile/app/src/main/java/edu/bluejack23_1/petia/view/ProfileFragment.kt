package edu.bluejack23_1.petia.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import edu.bluejack23_1.petia.R
import edu.bluejack23_1.petia.databinding.FragmentProfileBinding
import edu.bluejack23_1.petia.model.Comment
import edu.bluejack23_1.petia.model.Filter
import edu.bluejack23_1.petia.util.CommentAdapter
import edu.bluejack23_1.petia.util.SharedPreferencesManager
import edu.bluejack23_1.petia.viewmodel.AuthViewModel
import edu.bluejack23_1.petia.viewmodel.CommentViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentViewModel : CommentViewModel
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentArrayList: ArrayList<Comment>
    private lateinit var selectedImage: Uri
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        commentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)

        recyclerView = binding.commentRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.hasFixedSize()

        commentArrayList = arrayListOf()
        commentAdapter = CommentAdapter(commentArrayList, commentViewModel, viewModel, requireContext())
        recyclerView.adapter = commentAdapter

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val userID = sharedPreferencesManager.getChosenUserId()

        if(sharedPreferencesManager.getUserId() != sharedPreferencesManager.getChosenUserId()){
            binding.editUsernameBtn.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
            binding.changePassBtn.visibility = View.GONE
        }

        viewModel.getUserByID(userID) { user ->
            if (user != null) {
                Picasso.get().load(user.profileImage).into(binding.userProfile)
                binding.userName.text = user.username
                binding.emailLabel.text = "Email: "+user.email
                binding.nameLabel.text = "Name: "+user.fullname
                binding.phoneLabel.text = "Phone: "+user.phone
                binding.passwordLabel.text = "Password: ***********"

                if(user.role == "Shelter") {
                    binding.commentCardView.visibility = View.VISIBLE;
                }
            }
        }

        commentViewModel.commentLiveData.observe(viewLifecycleOwner) { c ->
            updateRecyclerView(c)
        }
        commentViewModel.loadComments(sharedPreferencesManager.getUserId())

        val logoutButton = view.findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            viewModel.logoutUser(requireContext())
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val commentBtn = view.findViewById<ImageButton>(R.id.postCommentBtn)
        val commentInputLayout: TextInputLayout = view.findViewById(R.id.commentInput)
        val commentEditText: EditText? = commentInputLayout.editText

        commentBtn.setOnClickListener {
            Log.d("Tes", "woi")
            commentViewModel.postComment(userID, sharedPreferencesManager.getUserId(), commentEditText?.text.toString());
            commentInputLayout.editText?.setText("")
            commentInputLayout.error = null
            commentAdapter.updateComments(Comment(userID, sharedPreferencesManager.getUserId(), commentEditText?.text.toString()))
        }

        binding.changePassBtn.setOnClickListener {
            showDialog()
        }

        binding.editUsernameBtn.setOnClickListener {
            showUsernameDialog()
        }

        binding.userProfile.setOnClickListener {
            showProfileDialog()
        }
    }

    private fun updateRecyclerView(comment: ArrayList<Comment>) {
        commentAdapter.updateData(comment)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_change_password)

        val closeBtn = dialog.findViewById<Button>(R.id.closeChangePassword)
        val submitBtn = dialog.findViewById<Button>(R.id.change_username_btn)

        val oldPasswordLayout = dialog.findViewById<TextInputLayout>(R.id.oldPasswordField)
        val newPasswordLayout = dialog.findViewById<TextInputLayout>(R.id.newPasswordField)
        val confPasswordLayout = dialog.findViewById<TextInputLayout>(R.id.confirmPasswordField)

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        submitBtn.setOnClickListener {
            viewModel.changePassword(oldPasswordLayout.editText?.text.toString(), newPasswordLayout.editText?.text.toString(), confPasswordLayout.editText?.text.toString()) { success, message ->
                if (success) {
                    dialog.dismiss()
                }
                else {
                    if(message.contentEquals("Old password cannot be empty.") || message.contentEquals("Old password is incorrect.")) {
                        oldPasswordLayout.error = message
                        newPasswordLayout.error = null
                        confPasswordLayout.error = null
                    }
                    else if(message.contentEquals("New password cannot be empty.") || message.contentEquals("Old password and new password cannot be the same.")) {
                        newPasswordLayout.error = message
                        oldPasswordLayout.error = null
                        confPasswordLayout.error = null
                    }
                    else if(message.contentEquals("New password and confirm password must match.")) {
                        confPasswordLayout.error = message
                        oldPasswordLayout.error = null
                        newPasswordLayout.error = null
                    }
                }
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun showUsernameDialog() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_change_username)

        val closeBtn = dialog.findViewById<Button>(R.id.closeChangeUsername)
        val submitBtn = dialog.findViewById<Button>(R.id.change_username_btn)

        val newUsernameLayout = dialog.findViewById<TextInputLayout>(R.id.newUsernameField)

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        submitBtn.setOnClickListener {
            viewModel.changeUsername(newUsernameLayout.editText?.text.toString()) { success, message ->
                if (success) {
                    dialog.dismiss()
                }
                else {
                    newUsernameLayout.error = message
                }
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun showProfileDialog() {
        dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.fragment_change_profile)

        val closeBtn = dialog?.findViewById<Button>(R.id.closeChangeProfile)
        val chooseImageButton: Button? = dialog?.findViewById(R.id.chooseImageButton)
        val uploadButton: Button? = dialog?.findViewById(R.id.uploadButton)
        val imagePreview: ImageView? = dialog?.findViewById(R.id.imagePreview)

        imagePreview?.setBackgroundResource(R.drawable.circle_background)
        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val userID = sharedPreferencesManager.getUserId()

        viewModel.getUserByID(userID) { user ->
            if (user != null) {
                Picasso.get().load(user.profileImage).into(dialog?.findViewById(R.id.imagePreview))
            }
        }

        chooseImageButton?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 100)
        }

        uploadButton?.setOnClickListener {
            selectedImage?.let { uri ->
                viewModel.changeProfileImage(uri) { success, message ->
                    if (success) {
                        dialog?.dismiss()
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }

        closeBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImage = data?.data!!

            val imagePreview: ImageView? = dialog?.findViewById(R.id.imagePreview)
            Log.d("File", selectedImage.toString())
            Picasso.get().load(selectedImage).into(imagePreview)
        }
    }
}