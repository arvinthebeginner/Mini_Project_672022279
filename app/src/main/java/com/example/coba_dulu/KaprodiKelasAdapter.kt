package com.example.coba_dulu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KaprodiKelasAdapter(
    private val kelasList: List<Kelas>
) : RecyclerView.Adapter<KaprodiKelasAdapter.KelasViewHolder>() {

    inner class KelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKelas: TextView = itemView.findViewById(R.id.textNamaKelas)
        val namaDosen: TextView = itemView.findViewById(R.id.textNamaDosen)
        val jumlahMahasiswa: TextView = itemView.findViewById(R.id.textJumlahMahasiswa)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelas_kaprodi, parent, false)
        return KelasViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val kelas = kelasList[position]
        holder.namaKelas.text = kelas.name
        holder.namaDosen.text = "Dosen: ${kelas.dosen}"
        val jumlahMahasiswa = kelas.mahasiswa?.size ?: 0
        holder.jumlahMahasiswa.text = "Jumlah Mahasiswa: $jumlahMahasiswa"

    }

    override fun getItemCount(): Int = kelasList.size
}

