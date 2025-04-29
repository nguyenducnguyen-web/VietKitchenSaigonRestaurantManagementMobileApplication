package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Admin")
data class Admin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hoTen: String,
    val email: String,
    val matKhau: String,
    val diaChi: String?,
    val soDienThoai: String?
)