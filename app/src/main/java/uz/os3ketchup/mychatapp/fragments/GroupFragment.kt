package uz.os3ketchup.mychatapp.fragments

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.*
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.GroupAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentGroupBinding
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Group
import java.lang.IllegalStateException


class GroupFragment : Fragment() {
    lateinit var binding: FragmentGroupBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(GROUP)
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

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

                    val groupAdapter = GroupAdapter(requireActivity(), groupList, navController)
                    binding.rvGroup.adapter = groupAdapter
                }catch (e:IllegalStateException){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}