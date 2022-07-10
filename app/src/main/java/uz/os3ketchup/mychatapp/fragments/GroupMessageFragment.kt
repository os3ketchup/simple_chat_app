package uz.os3ketchup.mychatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.GroupMessageAdapter
import uz.os3ketchup.mychatapp.adapters.MessageAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentGroupMessageBinding
import uz.os3ketchup.mychatapp.moduls.*
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP_CHAT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GroupMessageFragment : Fragment() {
    lateinit var binding: FragmentGroupMessageBinding
    lateinit var viewModel: MainViewModel
    lateinit var mAuth: FirebaseAuth
    lateinit var groupName: String
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupMessageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
        setLiveData()
        setMessageAdapter()
    }

    private fun setTabLayout() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference(GROUP)
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val group = child.getValue(Group::class.java)
                    if (group != null) {
                        if (group.fullName == groupName) {
                            binding.tvUserName.text = group.fullName
                            Glide.with(binding.root).load(group.imageLink)
                                .into(binding.ivProfile)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO()
            }
        })
    }

    private fun setMessageAdapter() {
        groupName = arguments?.getString(GROUP)!!
        mAuth = Firebase.auth
        val cUID = mAuth.currentUser?.uid!!
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val messageReference = firebaseDatabase.getReference(GROUP_CHAT)

        binding.btnSend.setOnClickListener {
            val currentTime: String = SimpleDateFormat("d MMM HH:mm", Locale.getDefault()).format(
                Date()
            )
            val key = messageReference.push().key!!
            val groupChatMessage = ChatMessage(
                text = binding.etMessage.text.toString().trim(),
                fromUID = cUID,
                toUID = groupName,
                timeSent = currentTime,
                pushKey = key
            )
            messageReference.child(key).setValue(groupChatMessage)
            binding.etMessage.text.clear()
        }
        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = ArrayList<ChatMessage>()
                for (child in snapshot.children) {
                    val chatMessage = child.getValue(ChatMessage::class.java)
                    if (chatMessage != null && groupName == chatMessage.toUID)
                        messageList.add(chatMessage)
                }
                val messageAdapter = GroupMessageAdapter(requireContext(),messageList, mAuth, groupName)
                binding.rvChatMessage.scrollToPosition(messageList.count() - 1)
                binding.rvChatMessage.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun setLiveData() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()
    }
}