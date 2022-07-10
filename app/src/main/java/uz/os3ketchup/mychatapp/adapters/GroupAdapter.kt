package uz.os3ketchup.mychatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.ItemGroupRvBinding
import uz.os3ketchup.mychatapp.databinding.ItemRvBinding
import uz.os3ketchup.mychatapp.fragments.REQUEST_CODE
import uz.os3ketchup.mychatapp.moduls.ChatMessage
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP
import uz.os3ketchup.mychatapp.moduls.Constants.GROUP_CHAT
import uz.os3ketchup.mychatapp.moduls.Group

class GroupAdapter(
    var context: Context, var list: List<Group>,
    var navController: NavController

) :
    RecyclerView.Adapter<GroupAdapter.VH>() {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference


    inner class VH(private var itemRV: ItemGroupRvBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(group: Group, position: Int) {
            itemRV.tvItemUser.text = group.fullName

            Glide.with(context).load(group.imageLink).into(itemRV.ivItemProfile)
            itemRV.root.setOnClickListener {
                navController.navigate(R.id.groupMessageFragment, bundleOf(GROUP to group.fullName))
            }
            itemRV.btnMenu.setOnClickListener {
                val popupMenu = PopupMenu(context, itemRV.btnMenu)
                popupMenu.inflate(R.menu.menu_group)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.item_edit -> {
                            navController.navigate(
                                R.id.editGroupFragment,
                                bundleOf("position" to group.groupId)
                            )
                            true
                        }
                        R.id.item_delete -> {

                            val builder: AlertDialog.Builder = context.let {
                                AlertDialog.Builder(it)
                            }
                            builder.setMessage("Are you sure to delete this group?").setTitle(group.fullName)
                            builder.apply {
                                setPositiveButton(
                                    "Yes"
                                ) { dialog, _ ->
                                    firebaseDatabase = FirebaseDatabase.getInstance()
                                    databaseReference = firebaseDatabase.getReference(GROUP)
                                    databaseReference.child(group.groupId).removeValue()
                                    val fbDatabase = FirebaseDatabase.getInstance()
                                    val dbReference = fbDatabase.getReference(GROUP_CHAT)
                                    dbReference.addValueEventListener(object :ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (child in snapshot.children){
                                                val groupChat = child.getValue(ChatMessage::class.java)
                                                if (groupChat?.toUID==group.fullName){
                                                    dbReference.child(groupChat.pushKey).removeValue()
                                                }

                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }
                                    })


                                    dialog.cancel()
                                }
                                setNegativeButton("No") { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                            }


                             builder.create().show()



                            true
                        }
                        else -> {
                            false
                        }

                    }
                }
                popupMenu.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemGroupRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}