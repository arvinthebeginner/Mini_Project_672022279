package com.example.coba_dulu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KaprodiFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editNamaKelas: EditText
    private lateinit var editNamaDosen: EditText
    private lateinit var buttonTambah: Button

    private lateinit var database: DatabaseReference
    private lateinit var kelasList: MutableList<Kelas>
    private lateinit var kelasAdapter: KaprodiKelasAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kaprodi, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewKelasKaprodi)
        editNamaKelas = view.findViewById(R.id.editTextNamaKelas)
        editNamaDosen = view.findViewById(R.id.editTextNamaDosen)
        buttonTambah = view.findViewById(R.id.buttonTambahKelas)

        database = FirebaseDatabase.getInstance().reference
        kelasList = mutableListOf()
        kelasAdapter = KaprodiKelasAdapter(kelasList)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutKaprodi)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = kelasAdapter

        swipeRefreshLayout.setOnRefreshListener {
            loadKelas()
        }

        buttonTambah.setOnClickListener {
            val namaKelas = editNamaKelas.text.toString().trim()
            val namaDosen = editNamaDosen.text.toString().trim()

            if (namaKelas.isEmpty() || namaDosen.isEmpty()) {
                Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kelasId = database.child("classes").push().key ?: return@setOnClickListener
            val kelasData = mapOf(
                "id" to kelasId,
                "name" to namaKelas,
                "dosen" to namaDosen,
                "mahasiswa" to emptyMap<String, Any>()
            )

            database.child("classes").child(kelasId).setValue(kelasData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Kelas berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    editNamaKelas.text.clear()
                    editNamaDosen.text.clear()
                    loadKelas()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal menambah kelas: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        loadKelas()
        return view
    }

    private fun loadKelas() {
        database.child("classes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()

                for (kelasSnapshot in snapshot.children) {
                    val id = kelasSnapshot.key ?: continue
                    val name = kelasSnapshot.child("name").getValue(String::class.java) ?: "-"
                    val dosen = kelasSnapshot.child("dosen").getValue(String::class.java) ?: "-"

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

                kelasAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data kelas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
