package com.example.coba_dulu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KelasAdapter(
    private val kelasList: List<Kelas>,
    private val uid: String,
    private val onMasukClicked: (Kelas) -> Unit
) : RecyclerView.Adapter<KelasAdapter.KelasViewHolder>() {

    inner class KelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKelas: TextView = itemView.findViewById(R.id.textNamaKelas)
        val namaDosen: TextView = itemView.findViewById(R.id.textNamaDosen)
        val nilaiText: TextView = itemView.findViewById(R.id.textNilai)
        val btnMasuk: Button = itemView.findViewById(R.id.buttonMasukKelas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas, parent, false)
        return KelasViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val kelas = kelasList[position]

        holder.namaKelas.text = kelas.name
        holder.namaDosen.text = "Dosen: ${kelas.dosen}"

        val sudahMasuk = kelas.mahasiswa?.containsKey(uid) == true

        if (sudahMasuk) {
            val nilai = kelas.mahasiswa?.get(uid)?.nilai
            holder.nilaiText.text = if (nilai == -1) "Nilai: Belum dinilai" else "Nilai: $nilai"
            holder.btnMasuk.visibility = View.GONE
        } else {
            holder.nilaiText.text = "Belum masuk kelas"
            holder.btnMasuk.visibility = View.VISIBLE
            holder.btnMasuk.setOnClickListener {
                it.isEnabled = false
                onMasukClicked(kelas)
            }
        }
    }

    override fun getItemCount(): Int = kelasList.size
}


