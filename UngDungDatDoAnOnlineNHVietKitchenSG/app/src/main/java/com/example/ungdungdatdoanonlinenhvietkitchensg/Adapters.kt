package com.example.ungdungdatdoanonlinenhvietkitchensg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemMonAnBinding

// Data class dùng chung cho chi tiết món ăn
data class MonAnChiTiet(
    val hinhAnh: String?,
    val tenMon: String?,
    val gia: Double,
    val soLuong: Int,
    val ngayDatHang: java.util.Date
)

// Adapter dùng chung để hiển thị danh sách món ăn
class ChiTietMonAnAdapter(private var monAnList: List<MonAnChiTiet>) :
    RecyclerView.Adapter<ChiTietMonAnAdapter.ChiTietViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChiTietViewHolder {
        val binding = ItemMonAnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChiTietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChiTietViewHolder, position: Int) {
        val monAn = monAnList[position]
        holder.bind(monAn)
    }

    override fun getItemCount(): Int = monAnList.size

    fun updateData(newList: List<MonAnChiTiet>) {
        monAnList = newList
        notifyDataSetChanged()
    }

    inner class ChiTietViewHolder(private val binding: ItemMonAnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(monAn: MonAnChiTiet) {
            Glide.with(binding.root.context).load(monAn.hinhAnh).into(binding.AnhMonAnDonHang)
            binding.TenMonAnDonHang.text = monAn.tenMon
            binding.GiaMonAnDonHang.text = "${monAn.gia} VNĐ"
            binding.txtSoLuongDat.text = "x${monAn.soLuong}"
        }
    }
}