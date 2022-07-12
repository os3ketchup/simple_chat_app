package uz.os3ketchup.mychatapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.MainActivity
import uz.os3ketchup.mychatapp.R
import uz.os3ketchup.mychatapp.databinding.FragmentVerifyBinding
import uz.os3ketchup.mychatapp.moduls.Constants
import uz.os3ketchup.mychatapp.moduls.Constants.PHONE_KEY
import java.util.concurrent.TimeUnit

class VerifyFragment : Fragment() {
    lateinit var binding: FragmentVerifyBinding
    lateinit var phoneNumber: String
    lateinit var mAuth: FirebaseAuth
    lateinit var mVerificationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progress.visibility = View.VISIBLE

        phoneNumber = "+" + arguments?.getString(PHONE_KEY)!!
        binding.tvPhoneNumber.text = phoneNumber
        mAuth = Firebase.auth
        sendCode()
        with(binding) {
            btnSubmit.setOnClickListener {
                val etCode = etCode.text.toString()
                if (etCode.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(mVerificationId, etCode)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
    }

    private fun sendCode() {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId
            /* resendToken = token*/
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnSuccessListener {
        }
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.progress.visibility = View.INVISIBLE
                    // Sign in success, update UI with the signed-in user's information
                    findNavController().navigate(R.id.profileFragment)
                    Toast.makeText(requireContext(), "  ${credential.smsCode}", Toast.LENGTH_SHORT)
                        .show()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}


