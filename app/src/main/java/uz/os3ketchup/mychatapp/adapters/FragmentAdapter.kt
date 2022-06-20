package uz.os3ketchup.mychatapp.adapters

import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.os3ketchup.mychatapp.fragments.ChatFragment
import uz.os3ketchup.mychatapp.fragments.GroupFragment

class FragmentAdapter(var context: Context, fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                ChatFragment()
            }
            1->{
                GroupFragment()
            }else->{
                throw Resources.NotFoundException("Position not found")
            }
        }
    }
}