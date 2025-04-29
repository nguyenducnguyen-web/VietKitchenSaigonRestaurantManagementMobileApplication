package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "DonHang", foreignKeys = [
    ForeignKey(entity = NguoiDung::class, parentColumns = ["id"], childColumns = ["idNguoiDung"])
])
data class DonHang(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idNguoiDung: Int,
    val hoTen: String,
    val diaChi: String,
    val soDienThoai: String,
    val phuongThucThanhToan: String = "Thanh toán khi nhận hàng",
    val tenChuThe: String?,
    val soThe: String?,
    val ngayHetHan: String?,
    val cvvNo: String?,
    val tongTien: Double,
    val trangThai: String = "Đang xử lý"
)