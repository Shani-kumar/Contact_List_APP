package com.example.contactlistapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactlistapp.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class MainActivity : AppCompatActivity(),NotesItemClicked {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var fstore: FirebaseFirestore
    private lateinit var items: ArrayList<usermodel>
    private lateinit var userid: String


//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu,menu)
//        return true
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId) {
//            R.id.Signoutbtn -> {
//                auth.signOut()
//                val pref = getSharedPreferences("SignIn", MODE_PRIVATE)
//                val editor = pref.edit()
//                editor.putBoolean("flag", false)
//                editor.apply()
//                val intent = Intent(this, activity_sign_in::class.java)
//                startActivity(intent)
//                finish()
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        fstore = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()

        setText()
        auth.currentUser?.let { retrieveNotesFromFirestore(it.uid) }

        binding.titlebar.toolbar.setOnMenuItemClickListener {
                    when(it.itemId) {
            R.id.Signoutbtn -> {
//                Toast.makeText(this,"sign out clicked ",Toast.LENGTH_LONG).show()
                auth.signOut()
                val pref = getSharedPreferences("SignIn", MODE_PRIVATE)
                val editor = pref.edit()
                editor.putBoolean("flag", false)
                editor.apply()
                val intent = Intent(this, activity_sign_in::class.java)
                startActivity(intent)
                finish()
               true
            }
            else -> false
        }
        }

    }

    private fun setText() {
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userid = currentUser.uid

            val collectionRef = fstore.collection("user")
            collectionRef.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    binding.submit.setOnClickListener {
                        val name = binding.input.text.toString()
                        val number = binding.inputNumber.text.toString()
                        binding.input.setText("")
                        binding.inputNumber.setText("")
                        addNoteToFirestore(name,number, userid)
                        retrieveNotesFromFirestore(userid)

                    }
                    retrieveNotesFromFirestore(userid)


                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
        } else {

            val intent = Intent(this, activity_sign_in::class.java)
            startActivity(intent)
            finish()
        }

    }



    private fun addNoteToFirestore(name: String, number:String, userId: String) {

        val um = usermodel(name,number, false, Timestamp(Date()), userId, "") // Initialize firestoreId as empty

        fstore.collection("note")
            .add(um)
            .addOnSuccessListener { documentReference ->
                val firestoreId = documentReference.id

                um.firestoreId = firestoreId

                fstore.collection("note").document(firestoreId)
                    .set(um)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()
                            retrieveNotesFromFirestore(userid)
                        } else {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
    }



    private fun retrieveNotesFromFirestore(userid: String) {
        val notesRef = fstore.collection("note").whereEqualTo("userid", userid).orderBy("completed",
            Query.Direction.ASCENDING)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        notesRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                //  Toast.makeText(this, "Error fetching notes}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            querySnapshot?.let { snapshot ->
                val notesList = ArrayList<usermodel>() // Use proper capitalization
                for (document in snapshot.documents) {
                    val note = document.toObject(usermodel::class.java)
                    note?.let { retrievedNote ->
                        notesList.add(retrievedNote)
                    }
                }

                binding.recyclerview.adapter = Notesadapter(notesList,this)
            }
        }
    }

    override fun onItemClicked(item: usermodel) {

    }


}