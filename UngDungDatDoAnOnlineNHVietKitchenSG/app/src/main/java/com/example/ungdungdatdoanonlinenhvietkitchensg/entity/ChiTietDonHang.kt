package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ChiTietDonHang", foreignKeys = [
    ForeignKey(entity = DonHang::class, parentColumns = ["id"], childColumns = ["idDonHang"]),
    ForeignKey(entity = MonAn::class, parentColumns = ["id"], childColumns = ["idMonAn"])
])
data class ChiTietDonHang(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idDonHang: Int,
    val idMonAn: Int,
    val soLuong: Int,
    val ngayDatHang: Date = Date()
)