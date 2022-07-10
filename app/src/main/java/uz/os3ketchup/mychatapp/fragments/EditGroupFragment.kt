package uz.os3ketchup.mychatapp.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.FragmentEditGroupBinding
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Group
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import java.io.EOFException
import java.io.IOException


class EditGroupFragment : Fragment() {
    lateinit var binding: FragmentEditGroupBinding
    private lateinit var viewModel: MainViewModel
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    lateinit var fileUri: Uri
    private var imageLink: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLivedata()
        val position = arguments?.getString("position")!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(GROUP)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val group = child.getValue(Group::class.java)
                    if (group?.groupId == position) {
                        binding.tvLogoChat.text = group.fullName
                        binding.etFirstName.setText(group.fullName)
                        imageLink = group.imageLink
                        Glide.with(binding.root).load(group.imageLink).into(binding.ivProfile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.item_check->{
                    if (binding.etFirstName.text.isNotEmpty()){
                        val group = Group(groupId = position, fullName = binding.etFirstName.text.toString(), imageLink = imageLink)
                        databaseReference.child(position).setValue(group)
                        findNavController().popBackStack()
                    }else{
                        Toast.makeText(context, "Please fill the gaps", Toast.LENGTH_SHORT).show()
                    }

                    true
                }
                R.id.item_photo_remove->{
                    binding.ivProfile.setImageResource(R.drawable.phot)
                    imageLink = ""
                    true
                }


                else->{
                    false
                }
            }
        }



        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

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
            1
        )
    }

    private fun setLivedata() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.data!=null){
           fileUri = data.data!!
            uploadImageToFirestore()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,fileUri)!!
                binding.ivProfile.setImageBitmap(bitmap)
                Glide.with(requireActivity()).load(fileUri).into(binding.ivProfile)
            }catch (e:IOException){
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
        storageReference = storage.reference.child("image_group/${System.currentTimeMillis()/10000}")

        storageReference.putFile(fileUri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Image successfully uploaded", Toast.LENGTH_SHORT)
                .show()
            val downloadUrl = it.metadata?.reference?.downloadUrl
            downloadUrl?.addOnSuccessListener { imageUri ->
                imageLink = imageUri.toString()
                Toast.makeText(requireContext(), "$imageUri", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Successfully completed", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Can't download uri", Toast.LENGTH_SHORT).show()
            }
        }
    }



}


