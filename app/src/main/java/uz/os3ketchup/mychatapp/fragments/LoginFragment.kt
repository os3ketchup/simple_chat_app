package uz.os3ketchup.mychatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.FragmentLoginBinding
import uz.os3ketchup.mychatapp.moduls.Constants.PHONE_KEY


class LoginFragment : Fragment() {
lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                if (etNumber.text.isNotEmpty()&&etNumber.text.toString().length<=12) {
                    findNavController().navigate(R.id.verifyFragment, bundleOf(PHONE_KEY to etNumber.text.toString()))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter your number correct order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

}