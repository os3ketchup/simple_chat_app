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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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


        binding.btnSubmit.setOnClickListener {
            val fullName =
                binding.etFirstName.text.toString()
                    .trim() + " " + binding.etLastName.text.toString().trim()
            val user = User(UID = uid!!, fullName = fullName, phoneNumber = phoneNumber!!)
            databaseRef.child(mAuth.uid!!).setValue(user)
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }


    }


}