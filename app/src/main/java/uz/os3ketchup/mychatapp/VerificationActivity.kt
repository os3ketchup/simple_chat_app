package uz.os3ketchup.mychatapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.databinding.ActivityVerificationBinding
import uz.os3ketchup.mychatapp.moduls.Constants.PHONE_KEY
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerificationBinding
    lateinit var phoneNumber: String
    lateinit var mAuth: FirebaseAuth
    lateinit var mVerificationId: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        phoneNumber ="+" +  intent.getStringExtra(PHONE_KEY)!!
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
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    override fun onRestart() {
        super.onRestart()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
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
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Toast.makeText(this, "  ${credential.smsCode}", Toast.LENGTH_SHORT).show()
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