package uz.os3ketchup.mychatapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.ItemRvVerticalBinding
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.User

class UserHorizontalAdapter(var context: Context, var list: List<User>, var navController: NavController) : RecyclerView.Adapter<UserHorizontalAdapter.VH>() {

    inner class VH(private var itemRV: ItemRvVerticalBinding) : RecyclerView.ViewHolder(itemRV.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(user: User) {
            itemRV.tvUser.text = user.firstName + " " + user.lastName
            Glide.with(context).load(user.imageLink).into(itemRV.ivItemProfile)
            itemRV.root.setOnClickListener {
                    navController.navigate(R.id.chatMessageFragment, bundleOf(USER to user.UID))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}