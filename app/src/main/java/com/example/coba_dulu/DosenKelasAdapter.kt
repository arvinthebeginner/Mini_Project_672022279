package com.example.coba_dulu

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference


class DosenKelasAdapter(
    private val kelasList: List<Kelas>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<DosenKelasAdapter.KelasViewHolder>() {

    inner class KelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKelas: TextView = itemView.findViewById(R.id.textViewNamaKelas)
        val layoutMahasiswa: LinearLayout = itemView.findViewById(R.id.layoutMahasiswa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelas_dosen, parent, false)
        return KelasViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val kelas = kelasList[position]
        holder.namaKelas.text = kelas.name
        holder.layoutMahasiswa.removeAllViews()

        val context = holder.itemView.context

        kelas.mahasiswa?.forEach { (uid, mahasiswaKelas) ->
            val horizontalLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            val namaMhs = TextView(context).apply {
                text = uid
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 2f)
            }

            val inputNilai = EditText(context).apply {
                hint = "Nilai"
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(if (mahasiswaKelas.nilai != -1) mahasiswaKelas.nilai.toString() else "")
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            }

            val btnSimpan = Button(context).apply {
                text = "Simpan"
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setOnClickListener {
                    val nilaiBaru = inputNilai.text.toString().toIntOrNull()
                    if (nilaiBaru != null) {
                        val ref = database.child("classes").child(kelas.id).child("mahasiswa").child(uid).child("nilai")
                        ref.setValue(nilaiBaru).addOnSuccessListener {
                            Toast.makeText(context, "Nilai disimpan", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Gagal simpan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Nilai tidak valid", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            horizontalLayout.addView(namaMhs)
            horizontalLayout.addView(inputNilai)
            horizontalLayout.addView(btnSimpan)
            holder.layoutMahasiswa.addView(horizontalLayout)
        }
    }

    override fun getItemCount(): Int = kelasList.size
}
