package com.example.coba_dulu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebase = FirebaseDatabase.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val toRegisterButton = findViewById<Button>(R.id.buttonToRegister)

        loginButton.setOnClickListener {
            val inputUsername = usernameEditText.text.toString().trim()
            val inputPassword = passwordEditText.text.toString().trim()

            firebase.reference.child("users").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var loginSuccess = false

                    for (userSnapshot in snapshot.children) {
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        val password = userSnapshot.child("password").getValue(String::class.java)

                        if (username == inputUsername && password == inputPassword) {
                            loginSuccess = true

                            val role = userSnapshot.child("role").getValue(String::class.java) ?: "student"
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)

                            intent.putExtra("username", inputUsername)
                            intent.putExtra("role", role)

                            startActivity(intent)
                            finish()
                            break
                        }
                    }

                    if (!loginSuccess) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Username atau password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        toRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

}

