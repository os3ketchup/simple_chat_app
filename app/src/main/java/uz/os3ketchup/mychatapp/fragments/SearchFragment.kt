package uz.os3ketchup.mychatapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.GroupAdapter
import uz.os3ketchup.mychatapp.adapters.UserAdapter
import uz.os3ketchup.mychatapp.adapters.UserHorizontalAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentSearchBinding
import uz.os3ketchup.mychatapp.moduls.Constants
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.Group
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import uz.os3ketchup.mychatapp.moduls.User
import java.lang.IllegalStateException


class SearchFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: MainViewModel
    lateinit var mAuth: FirebaseAuth
    lateinit var groupAdapter:GroupAdapter
    lateinit var userHorizontalAdapter: UserHorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()


        retrieveGroup()
                    retrieveUser()
                    binding.etSearch.addTextChangedListener(object :TextWatcher{

                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {

                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            searchingForUser(p0.toString())
                            searchingForGroup(p0.toString())

                        }

                        override fun afterTextChanged(p0: Editable?) {

                        }
                    })




    }

    private fun searchingForUser(str: String) {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        val  firebaseDatabase = FirebaseDatabase.getInstance()
        val currentUser = mAuth.currentUser?.uid
        val queryReference = firebaseDatabase.getReference(USER).orderByChild("firstName")
            .startAt(str)
            .endAt(str + "\uf8ff")
        queryReference.addValueEventListener(object :ValueEventListener{
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
                try {
                    userHorizontalAdapter = UserHorizontalAdapter(requireContext(),list,navController)
                    binding.rvSearchUser.adapter = userHorizontalAdapter
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })

    }

    private fun retrieveUser() {
       val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

      val  firebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = Firebase.auth
        val currentUser = mAuth.currentUser?.uid
       val databaseReference = firebaseDatabase.getReference(Constants.USER)
        databaseReference.addValueEventListener(object :ValueEventListener{
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
                try {
                     userHorizontalAdapter = UserHorizontalAdapter(requireContext(),list,navController)

                    binding.rvSearchUser.adapter = userHorizontalAdapter
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }

    private fun retrieveGroup() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference(Constants.GROUP)
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val groupList = ArrayList<Group>()
                for (child in snapshot.children) {
                    val group = child.getValue(Group::class.java)
                    if (group != null) {
                        groupList.add(group)
                    }
                }
                    try {
                        groupAdapter   = GroupAdapter(requireActivity(), groupList, navController)

                        binding.rvSearch.adapter = groupAdapter
                    }catch (e:IllegalStateException){
                        e.printStackTrace()
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }

    private fun searchingForGroup(str: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val queryReference = firebaseDatabase.getReference(Constants.GROUP)
            .orderByChild("fullName")
            .startAt(str)
            .endAt(str + "\uf8ff")
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        queryReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val groupList = ArrayList<Group>()
                for (child in snapshot.children) {
                    val group = child.getValue(Group::class.java)
                    if (group != null) {
                        groupList.add(group)
                    }
                }
                try {
                    groupAdapter = GroupAdapter(requireActivity(), groupList, navController)

                    binding.rvSearch.adapter = groupAdapter
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }

}




