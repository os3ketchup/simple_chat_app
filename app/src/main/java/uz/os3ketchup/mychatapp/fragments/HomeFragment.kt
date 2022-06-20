package uz.os3ketchup.mychatapp.fragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import uz.os3ketchup.mychatapp.MainActivity
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.adapters.FragmentAdapter
import uz.os3ketchup.mychatapp.databinding.FragmentHomeBinding
import uz.os3ketchup.mychatapp.moduls.MainViewModel


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLiveData()
        setViewPager()
    }

    private fun setViewPager() {
        val fragmentAdapter = FragmentAdapter(requireContext(), requireActivity())
        binding.rvAdapter.adapter = fragmentAdapter
        TabLayoutMediator(binding.tabLayout, binding.rvAdapter) { tab, index ->
            run {
                when (index) {
                    0 -> {
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_persons)
                        /*tab.text = "Chats"*/
                    }
                    1 -> {
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_super_group)
                       /* tab.text = "Groups"*/
                    }
                    else -> {
                        throw Resources.NotFoundException("Fragment error ${System.currentTimeMillis()}")
                    }
                }
            }
        }.attach()
    }

    private fun setLiveData() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goHome()
    }
}