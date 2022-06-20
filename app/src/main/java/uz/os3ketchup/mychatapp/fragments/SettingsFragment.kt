package uz.os3ketchup.mychatapp.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.FragmentSettingsBinding
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import uz.os3ketchup.mychatapp.moduls.User
import java.io.IOException

const val REQUEST_CODE = 1

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: MainViewModel
    lateinit var lastName: String
    lateinit var fileUri: Uri
    lateinit var currentUID: String
     private var imageLink:String = ""
    lateinit var user: User
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setLiveData()
        setDefaultLayout()
        handlePhoto()
        confirmChanges()
    }
    private fun confirmChanges() {
        binding.topAppBar.setOnMenuItemClickListener {
            menuItem->
            when(menuItem.itemId){
                R.id.item_check->{
                    val fullName =
                        binding.etFirstName.text.toString()
                            .trim() + " " + binding.etLastName.text.toString().trim()
                    user = if (imageLink.isNotEmpty()){
                        User(UID = currentUID, fullName = fullName, phoneNumber = mAuth.currentUser?.phoneNumber!!, imageLink = imageLink)
                    }else{
                        User(UID = currentUID, fullName = fullName, phoneNumber = mAuth.currentUser?.phoneNumber!!)
                    }
                    databaseReference.child(mAuth.uid!!).setValue(user)
                    findNavController().navigate(R.id.homeFragment)

                    true
                }
                R.id.item_photo_remove->{

                    true
                }
                else->{
                    false
                }

            }
        }
    }

    private fun handlePhoto() {
        binding.ivProfile.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select image from here..."),
            REQUEST_CODE
        )
    }

    private fun setDefaultLayout() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = Firebase.auth
        databaseReference = firebaseDatabase.getReference(USER)
        currentUID = mAuth.currentUser?.uid!!
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        if (user.UID == currentUID) {
                            imageLink = user.imageLink
                            val fullNameDivider = user.fullName.split(" ")
                            val firstName = fullNameDivider[0]
                            if (fullNameDivider[1].isNotEmpty()) {
                                lastName = fullNameDivider[1]
                                binding.etLastName.setText(lastName)
                            }
                            binding.etFirstName.setText(firstName)
                            Glide.with(requireActivity()).load(user.imageLink).into(binding.ivProfile)

                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }

    private fun setLiveData() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data!!
            uploadImageToFirestore()
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                binding.ivProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirestore() {
        //code for showing progressDialog while uploading
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading")
        progressDialog.show()
        //initiate Firestore reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("image/$currentUID")

        storageReference.putFile(fileUri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show()
            val downloadUrl = it.metadata?.reference?.downloadUrl
            downloadUrl?.addOnSuccessListener {imageUri->
               imageLink = imageUri.toString()
                Toast.makeText(requireContext(), "$imageUri", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
        }.addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireContext(), "Successfully completed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Can't download uri", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
