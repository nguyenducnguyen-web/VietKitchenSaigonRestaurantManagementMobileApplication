package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "MonAn")
data class MonAn(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenMon: String,
    val gia: Double,
    val hinhAnh: String?,
    val moTa: String?,
    val soLuong: Int,
    val nguyenLieu: String?,
    val ngayThem: Date = Date()
)