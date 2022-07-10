package uz.os3ketchup.mychatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.os3ketchup.mychatapp.databinding.ActivityMainBinding
import uz.os3ketchup.mychatapp.moduls.Constants.USER
import uz.os3ketchup.mychatapp.moduls.MainViewModel
import uz.os3ketchup.mychatapp.moduls.User

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.searchFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.condition.observe(this) {
            when (it) {
                "settings" -> {
                    binding.appBar.visibility = View.GONE
                }
                "home" -> {
                    binding.appBar.visibility = View.VISIBLE
                }
            }

        }
        setLayout()

    }


    private fun setLayout() {
        // set header layout of navigation drawer
        val header = binding.navigationView.inflateHeaderView(R.layout.header_layout)
        val tvNumber = header.findViewById<TextView>(R.id.tv_number_profile)
        val tvName = header.findViewById<TextView>(R.id.tv_header_name)
        val imageHeader = header.findViewById<ImageView>(R.id.iv_header)
        // set viewpager

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        binding.navigationView.setupWithNavController(navController)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference(USER)
        mAuth = Firebase.auth
        val uid = mAuth.currentUser?.uid
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (uid == user?.UID) {
                        tvName.text = user?.firstName + " " + user?.lastName
                        tvNumber.text = user?.phoneNumber
                        Glide.with(this@MainActivity).load(user?.imageLink).into(imageHeader)
                        /*if (user?.imageLink!!.isNotEmpty()) {
                            Glide.with(this@MainActivity).load(user.imageLink).into(imageHeader)
                        } else {
                            Toast.makeText(this@MainActivity, "empty", Toast.LENGTH_SHORT).show()
                        }*/
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO()
            }
        })
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            when (menuItem.itemId) {
                R.id.settingsFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.settingsFragment)
                    binding.drawerLayout.close()
                }
                R.id.log_out -> {
                    mAuth.signOut()
                    if (mAuth.currentUser == null) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish() // destroy login so user can't come back with back button
                    }
                }
            }

            menuItem.isChecked = true
            /* binding.drawerLayout.close()*/
            true
        }


    }


}