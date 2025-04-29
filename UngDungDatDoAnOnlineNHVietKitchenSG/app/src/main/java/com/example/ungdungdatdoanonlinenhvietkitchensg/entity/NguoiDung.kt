package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NguoiDung")
data class NguoiDung(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hoTen: String,
    val email: String,
    val matKhau: String,
    val diaChi: String?,
    val soDienThoai: String?
)
