package com.example.contactlistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactlistapp.databinding.ActivitySignUpBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class activity_sign_up : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userid:String

    private lateinit var fstore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide();

        fstore= FirebaseFirestore.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this,activity_sign_in::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            userid= auth.currentUser!!.uid
                            val um:usermodel
                            um= usermodel(email,"",false, Timestamp(Date()),userid,"")
                            fstore.collection("user").document(userid).set(um).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                }
                            }

                            val intent = Intent(this, activity_sign_in::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}