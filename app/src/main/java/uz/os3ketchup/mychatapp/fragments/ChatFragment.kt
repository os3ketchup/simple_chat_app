package uz.os3ketchup.mychatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.UserAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentChatBinding
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.User


class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController



        firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = Firebase.auth
        val currentUser = mAuth.currentUser?.uid
        databaseReference = firebaseDatabase.getReference(USER)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user?.UID!=currentUser){
                        if (user!=null){
                            list.add(user)
                        }
                    }
                }
                val userAdapter = UserAdapter(requireContext(),list,navController)
                val layoutManager = LinearLayoutManager.VERTICAL
                val dividerItemDecoration = DividerItemDecoration(
                    binding.rvChat.context,
                    layoutManager
                )
                 binding.rvChat.addItemDecoration(dividerItemDecoration)
                binding.rvChat.adapter = userAdapter
            }


            override fun onCancelled(error: DatabaseError) {
                    error.message
            }
        })
    }


}