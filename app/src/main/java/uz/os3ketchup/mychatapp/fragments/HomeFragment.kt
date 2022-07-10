package uz.os3ketchup.mychatapp.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.os3ketchup.mychatapp.MainActivity
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.FragmentAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentHomeBinding
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Group
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import uz.os3ketchup.mychatapp.moduls.User
import java.io.IOException
import java.lang.Exception


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: MainViewModel
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var addImage: ImageView
    lateinit var mAuth: FirebaseAuth
    private lateinit var fileUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var imageLink: String = ""
    var incrementer = 0
    var uploaded = false
    lateinit var group: Group
    var unique = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(GROUP)

        databaseReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Group>()
                for (child in snapshot.children) {
                    val gr = child.getValue(Group::class.java)
                    if (gr != null) {
                        list.add(gr)
                    }
                }
                try {
                    incrementer = list[list.size - 1].groupId.toInt()
                    ++incrementer
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setLiveData()
        setViewPager()
        setAdapter()


    }

    private fun setAdapter() {
        binding.rvAdapter.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        viewModel.goChat()
                        binding.fab.visibility = View.INVISIBLE
                    }
                    1 -> {
                        binding.fab.visibility = View.VISIBLE
                        viewModel.goGroup()
                        binding.fab.setOnClickListener {
                            val dialogBuilder = AlertDialog.Builder(requireContext()).create()
                            val dialogView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.item_dialog, null, false)
                            val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)
                            val btnCancel = dialogView.findViewById<Button>(R.id.btn_dismiss)
                            val etGroup = dialogView.findViewById<EditText>(R.id.et_group)
                            addImage = dialogView.findViewById<ShapeableImageView>(R.id.image)

                            addImage.setOnClickListener {
                                selectImage()
                            }

                            btnAccept.setOnClickListener {

                                mAuth = Firebase.auth
                                val currentUID = mAuth.currentUser?.uid

                                if (etGroup.text.toString().isNotEmpty()) {


                                    group = if (uploaded) {
                                        Group(
                                            groupId = "$incrementer",
                                            fullName = etGroup.text.toString().trim(),
                                            imageLink = imageLink,
                                            currentUID = currentUID!!
                                        )
                                    } else {
                                        Group(
                                            groupId = "$incrementer",
                                            fullName = etGroup.text.toString().trim(),
                                            currentUID = currentUID!!
                                        )
                                    }

                                    uploaded = false

                                    databaseReference.child(incrementer.toString()).setValue(group)
                                    dialogBuilder.cancel()


                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please fill the gaps !!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }
                            btnCancel.setOnClickListener {
                                dialogBuilder.dismiss()
                            }
                            dialogBuilder.setView(dialogView)
                            dialogBuilder.show()
                        }
                    }
                }
            }
        })
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

    private fun setViewPager() {
        val fragmentAdapter = FragmentAdapter(requireContext(), requireActivity())
        binding.rvAdapter.adapter = fragmentAdapter
        TabLayoutMediator(binding.tabLayout, binding.rvAdapter) { tab, index ->
            run {
                when (index) {
                    0 -> {

                        tab.icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_persons)

                    }
                    1 -> {

                        tab.icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_super_group)

                    }
                    else -> {
                        throw Resources.NotFoundException("Fragment error ${System.currentTimeMillis()}")
                    }
                }
            }
        }.attach()
    }

    private fun setLiveData() {

        viewModel.goHome()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data!!
            uploadImageToFireStore()
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                addImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFireStore() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading")
        progressDialog.show()
        //initiate Firestore reference

        mAuth = Firebase.auth
        val cUID = mAuth.currentUser?.uid!!
        storage = FirebaseStorage.getInstance()
        storageReference =
            storage.reference.child("image_group/${System.currentTimeMillis() / 10000}")
        storageReference.putFile(fileUri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Image successfully uploaded", Toast.LENGTH_SHORT)
                .show()
            val downloadUrl = it.metadata?.reference?.downloadUrl
            downloadUrl?.addOnSuccessListener { imageUri ->
                imageLink = imageUri.toString()
                Toast.makeText(requireContext(), "$imageUri", Toast.LENGTH_SHORT).show()
                uploaded = true
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