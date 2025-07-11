package com.example.coba_dulu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DosenFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var kelasAdapter: DosenKelasAdapter
    private lateinit var kelasList: MutableList<Kelas>
    private lateinit var database: DatabaseReference
    private var uid: String? = null
    private lateinit var namaDosenText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dosen, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewKelasDosen)
        recyclerView.layoutManager = LinearLayoutManager(context)

        namaDosenText = view.findViewById(R.id.textViewNamaDosen)

        uid = arguments?.getString("uid")
        database = FirebaseDatabase.getInstance().reference
        kelasList = mutableListOf()

        uid?.let {
            namaDosenText.text = "Halo, $it"
            loadKelasUntukDosen(it)
        }

        return view
    }

    private fun loadKelasUntukDosen(dosenId: String) {
        database.child("classes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()

                for (kelasSnapshot in snapshot.children) {
                    val id = kelasSnapshot.key ?: continue
                    val name = kelasSnapshot.child("name").getValue(String::class.java) ?: "Tanpa Nama"
                    val dosen = kelasSnapshot.child("dosen").getValue(String::class.java) ?: continue

                    if (dosen == dosenId) {
                        val mahasiswaMap = mutableMapOf<String, MahasiswaKelas>()
                        val mahasiswaSnapshot = kelasSnapshot.child("mahasiswa")

                        for (mhs in mahasiswaSnapshot.children) {
                            val mhsId = mhs.key ?: continue
                            val nilai = mhs.child("nilai").getValue(Int::class.java) ?: -1
                            mahasiswaMap[mhsId] = MahasiswaKelas(nilai)
                        }

                        val kelas = Kelas(id, name, dosen, mahasiswaMap)
                        kelasList.add(kelas)
                    }
                }

                kelasAdapter = DosenKelasAdapter(kelasList, database)
                recyclerView.adapter = kelasAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat kelas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

