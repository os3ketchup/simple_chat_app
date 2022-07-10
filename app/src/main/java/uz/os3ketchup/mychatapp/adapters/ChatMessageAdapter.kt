package uz.os3ketchup.mychatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.databinding.ItemFromBinding
import uz.os3ketchup.mychatapp.databinding.ItemToBinding
import uz.os3ketchup.mychatapp.moduls.ChatMessage

class MessageAdapter(
    var list: List<ChatMessage>, var firebaseAuth: FirebaseAuth,
    var toUID: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVH(var itemFromBinding: ItemFromBinding) :
        RecyclerView.ViewHolder(itemFromBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemFromBinding.tvMessage.text = chatMessage.text
            itemFromBinding.tvCurrentTime.text = chatMessage.timeSent
        }
    }

    inner class ToVH(var itemToBinding: ItemToBinding) :
        RecyclerView.ViewHolder(itemToBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemToBinding.tvMessage.text = chatMessage.text
            itemToBinding.tvCurrentTime.text = chatMessage.timeSent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            FromVH(ItemFromBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ToVH(ItemToBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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