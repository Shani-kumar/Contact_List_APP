package com.example.contactlistapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactlistapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class activity_sign_in : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth;
    private lateinit var useremail:String
    private lateinit var userpassword:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = getSharedPreferences("SignIn", MODE_PRIVATE);
        val check = pref.getBoolean("flag",false)
        if(check){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener{
            val intent= Intent(this,activity_sign_up::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            useremail=binding.emailLayouttext.text.toString()
            userpassword=binding.passwordLayouttext.text.toString()
            if(useremail.isBlank()){
                binding.emailLayout.error="Email can't be Blank"
            }
            if(userpassword.isEmpty()){
                binding.passwordLayout.error="Password can't be empty"
            }
            if(Checkformdetails(useremail,userpassword)){

                auth.signInWithEmailAndPassword(useremail, userpassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val pref = getSharedPreferences("SignIn", MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putBoolean("flag", true)
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("Email",useremail)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error while signing", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



    }
    private fun Checkformdetails(useremail:String,userpassword:String): Boolean {

        return !(useremail.isBlank() || userpassword.isEmpty())
    }

}