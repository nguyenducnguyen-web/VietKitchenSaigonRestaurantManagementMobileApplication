package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ChiTietGioHang", foreignKeys = [
    ForeignKey(entity = GioHang::class, parentColumns = ["id"], childColumns = ["idGioHang"]),
    ForeignKey(entity = MonAn::class, parentColumns = ["id"], childColumns = ["idMonAn"])
])
data class ChiTietGioHang(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idGioHang: Int,
    val idMonAn: Int,
    val soLuong: Int,
) : Serializable