package com.example.coba_dulu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var kelasAdapter: KelasAdapter
    private lateinit var kelasList: MutableList<Kelas>
    private lateinit var database: DatabaseReference
    private lateinit var btnSemuaKelas: Button
    private lateinit var btnKelasSaya: Button
    private var uid: String? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var toggleState: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student, container, false)

        uid = arguments?.getString("uid")

        val textViewNama = view.findViewById<TextView>(R.id.textViewNamaPengguna)
        textViewNama.text = "Halo, ${uid ?: "Pengguna"}"

        recyclerView = view.findViewById(R.id.recyclerViewKelas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        btnSemuaKelas = view.findViewById(R.id.buttonSemuaKelas)
        btnKelasSaya = view.findViewById(R.id.buttonKelasSaya)
        swipeRefresh = view.findViewById(R.id.swipeRefreshLayout)

        database = FirebaseDatabase.getInstance().reference
        kelasList = mutableListOf()

        toggleState = true
        loadKelas(showAll = true)
        btnSemuaKelas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue1))
        btnKelasSaya.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

        btnSemuaKelas.setOnClickListener {
            toggleState = true
            loadKelas(showAll = true)
            btnSemuaKelas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue1))
            btnKelasSaya.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        btnKelasSaya.setOnClickListener {
            toggleState = false
            loadKelas(showAll = false)
            btnKelasSaya.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue1))
            btnSemuaKelas.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        swipeRefresh.setOnRefreshListener {
            loadKelas(toggleState)
            swipeRefresh.isRefreshing = false
        }

        return view
    }

    private fun loadKelas(showAll: Boolean) {
        database.child("classes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()

                for (kelasSnapshot in snapshot.children) {
                    val id = kelasSnapshot.key ?: continue
                    val name = kelasSnapshot.child("name").getValue(String::class.java) ?: "Tanpa Nama"
                    val dosen = kelasSnapshot.child("dosen").getValue(String::class.java) ?: "Tidak diketahui"

                    val mahasiswaMap = mutableMapOf<String, MahasiswaKelas>()
                    val mahasiswaSnapshot = kelasSnapshot.child("mahasiswa")

                    for (mhs in mahasiswaSnapshot.children) {
                        val mhsId = mhs.key ?: continue
                        val nilai = mhs.child("nilai").getValue(Int::class.java) ?: -1
                        mahasiswaMap[mhsId] = MahasiswaKelas(nilai)
                    }

                    val kelas = Kelas(id, name, dosen, mahasiswaMap)
                    val isStudentInClass = mahasiswaMap.containsKey(uid)

                    if (showAll || isStudentInClass) {
                        kelasList.add(kelas)
                    }
                }

                kelasAdapter = KelasAdapter(kelasList, uid ?: "") { kelas ->
                    masukKelas(kelas.id)
                }

                recyclerView.adapter = kelasAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat kelas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun masukKelas(classId: String) {
        val currentUid = uid ?: return
        val mahasiswaRef = database.child("classes").child(classId).child("mahasiswa").child(currentUid)

        val data = MahasiswaKelas(nilai = -1)

        mahasiswaRef.setValue(data)
            .addOnSuccessListener {
                Log.d("MasukKelas", "Berhasil masuk: $currentUid ke $classId")
                Toast.makeText(context, "Berhasil masuk kelas", Toast.LENGTH_SHORT).show()

                loadKelas(toggleState)
            }
            .addOnFailureListener {
                Log.e("MasukKelas", "Gagal masuk: ${it.message}")
                Toast.makeText(context, "Gagal masuk kelas", Toast.LENGTH_SHORT).show()
            }
    }
}


