package uz.os3ketchup.mychatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.databinding.ItemFromBinding
import uz.os3ketchup.mychatapp.databinding.ItemToBinding
import uz.os3ketchup.mychatapp.databinding.ItemToGroupBinding
import uz.os3ketchup.mychatapp.moduls.ChatMessage
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.Group
import uz.os3ketchup.mychatapp.moduls.User

class GroupMessageAdapter(var context: Context, var list: List<ChatMessage>, var firebaseAuth: FirebaseAuth,var toUID: String) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVH(var itemFromBinding: ItemFromBinding) :
        RecyclerView.ViewHolder(itemFromBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemFromBinding.tvMessage.text = chatMessage.text
            itemFromBinding.tvCurrentTime.text = chatMessage.timeSent

        }
    }

    inner class ToVH(var itemToGroupBinding: ItemToGroupBinding) :
        RecyclerView.ViewHolder(itemToGroupBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemToGroupBinding.tvMessage.text = chatMessage.text
            itemToGroupBinding.tvTime.text = chatMessage.timeSent
           val firebaseDatabase = FirebaseDatabase.getInstance()
            val reference = firebaseDatabase.getReference(USER)
            reference.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        val user = child.getValue(User::class.java)
                        if (user?.UID==chatMessage.fromUID){
                            Glide.with(context).load(user.imageLink).into(itemToGroupBinding.ivProfile)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            FromVH(ItemFromBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ToVH(ItemToGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVH = holder as FromVH
            fromVH.onBind(list[position])
        } else {
            (holder as ToVH).onBind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun getItemViewType(position: Int): Int {
        firebaseAuth = Firebase.auth
        val uid = firebaseAuth.currentUser?.uid

        return if (list[position].fromUID == uid) {
            1
        } else if (list[position].toUID == toUID) {
            2
        } else {
            3
        }


    }


}