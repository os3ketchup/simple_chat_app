package uz.os3ketchup.mychatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.ItemRvBinding
import uz.os3ketchup.mychatapp.moduls.User

class UserAdapter(var list: List<User>,var navController: NavController) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(private var itemRV: ItemRvBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(user: User) {
            itemRV.tvItemUser.text = user.fullName
            itemRV.root.setOnClickListener {
                    navController.navigate(R.id.chatMessageFragment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}