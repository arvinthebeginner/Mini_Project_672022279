package com.example.coba_dulu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("username") ?: "unknown"
        role = intent.getStringExtra("role") ?: "student"

        loadRoleFragment()
    }

    private fun loadRoleFragment() {
        val bundle = Bundle().apply {
            putString("uid", username)
        }

        val fragment = when (role) {
            "student" -> StudentFragment().apply { arguments = bundle }
            "dosen" -> DosenFragment().apply { arguments = bundle }
            "kaprodi" -> KaprodiFragment().apply { arguments = bundle }
            else -> Fragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}



