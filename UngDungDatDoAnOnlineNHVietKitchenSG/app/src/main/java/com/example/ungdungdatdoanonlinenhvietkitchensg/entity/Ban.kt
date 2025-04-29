package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Ban")
data class Ban(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenKhachDat: String,         // Bắt buộc nhập
    val soDienThoai: String,         // Bắt buộc nhập
    val soLuongKhach: Int,           // Bắt buộc nhập
    val trangThai: String = "Đang xử lý", // Giá trị mặc định
    val ngayDat: Date,
    val ngayThem: Date = Date()
)