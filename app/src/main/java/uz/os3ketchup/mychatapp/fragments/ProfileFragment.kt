package uz.os3ketchup.mychatapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.MainActivity
import uz.os3ketchup.mychatapp.databinding.FragmentProfileBinding
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.User


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
     var imageLink: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference(USER)
        mAuth = Firebase.auth
        val uid = mAuth.currentUser?.uid
        val phoneNumber = mAuth.currentUser?.phoneNumber

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference(USER)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        if (user.UID == uid) {
                            imageLink = user.imageLink
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.btnSubmit.setOnClickListener {
            if (binding.etFirstName.text.toString().isNotEmpty()){

                val user = User(
                    UID = uid!!,
                    phoneNumber = phoneNumber!!,
                    firstName = binding.etFirstName.text.toString(),
                    lastName = binding.etLastName.text.toString(),
                    imageLink = imageLink
                )
                databaseRef.child(mAuth.uid!!).setValue(user)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(context, "First name shouldn't be empty!", Toast.LENGTH_SHORT).show()
            }

        }


    }


}