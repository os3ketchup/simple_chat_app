package uz.os3ketchup.mychatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.MessageAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentChatBinding
import uz.os3ketchup.mychatapp.databinding.FragmentChatMessageBinding
import uz.os3ketchup.mychatapp.moduls.ChatMessage
import uz.os3ketchup.mychatapp.moduls.Constants.CHAT
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import uz.os3ketchup.mychatapp.moduls.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatMessageFragment : Fragment() {
    lateinit var binding: FragmentChatMessageBinding
    lateinit var viewModel: MainViewModel
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var toUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toUID = arguments?.getString(USER)!!
        setLiveData()
        setTabLayout()
        setMessageAdapter()
    }

    private fun setMessageAdapter() {
        mAuth = Firebase.auth
        val cUID = mAuth.currentUser?.uid!!
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val messageReference = firebaseDatabase.getReference(CHAT)
        binding.btnSend.setOnClickListener {
            val currentTime: String = SimpleDateFormat("d MMM HH:mm", Locale.getDefault()).format(
                Date()
            )
            val chatMessage =
                ChatMessage(
                    binding.etMessage.text.toString(),
                    fromUID = cUID,
                    toUID = toUID,
                    timeSent = currentTime
                )
            messageReference.child(messageReference.push().key!!).setValue(chatMessage)
            binding.etMessage.text.clear()
        }
        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = ArrayList<ChatMessage>()
                for (child in snapshot.children) {
                    val chatMessage = child.getValue(ChatMessage::class.java)
                    if (chatMessage != null && ((chatMessage.fromUID == cUID && chatMessage.toUID == toUID) || (chatMessage.fromUID == toUID && chatMessage.toUID == cUID)))
                        messageList.add(chatMessage)
                }
                val messageAdapter = MessageAdapter(messageList, mAuth, toUID)
                binding.rvChatMessage.scrollToPosition(messageList.count() - 1)
                binding.rvChatMessage.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setTabLayout() {

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference(USER)
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        if (user.UID == toUID) {
                            binding.tvUserName.text = user.firstName + " " + user.lastName
                            Glide.with(requireContext()).load(user.imageLink)
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

    private fun setLiveData() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()
    }


}