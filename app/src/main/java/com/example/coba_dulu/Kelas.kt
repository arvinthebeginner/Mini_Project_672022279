package com.example.coba_dulu

data class Kelas(
    val id: String = "",
    val name: String = "",
    val dosen: String = "",
    val mahasiswa: Map<String, MahasiswaKelas>? = null
) {
    val jumlahMahasiswa: Int
        get() = mahasiswa?.size ?: 0
}

