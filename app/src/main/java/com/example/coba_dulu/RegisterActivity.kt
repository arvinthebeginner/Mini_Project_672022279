package com.example.coba_dulu

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebase = FirebaseDatabase.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.editTextNewUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextNewPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegisterUser)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebase.reference.child("users").orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(this@RegisterActivity, "Username sudah ada", Toast.LENGTH_SHORT).show()
                        } else {
                            val newUser = mapOf(
                                "username" to username,
                                "password" to password,
                                "role" to "student"
                            )
                            val userId = firebase.reference.child("users").push().key!!
                            firebase.reference.child("users").child(userId).setValue(newUser)
                                .addOnSuccessListener {
                                    Toast.makeText(this@RegisterActivity, "Berhasil register", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@RegisterActivity, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@RegisterActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

}
