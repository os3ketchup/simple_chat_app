package uz.os3ketchup.mychatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.moduls.MainViewModel


class ChatMessageFragment : Fragment() {
    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLiveData()
    }

    private fun setLiveData() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.goSettings()
    }


}