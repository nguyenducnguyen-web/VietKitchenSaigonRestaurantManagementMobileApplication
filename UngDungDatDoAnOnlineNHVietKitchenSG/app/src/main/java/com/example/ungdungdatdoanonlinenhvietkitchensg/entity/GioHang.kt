package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "GioHang", foreignKeys = [
    ForeignKey(entity = NguoiDung::class, parentColumns = ["id"], childColumns = ["idNguoiDung"])
])
data class GioHang(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idNguoiDung: Int
)