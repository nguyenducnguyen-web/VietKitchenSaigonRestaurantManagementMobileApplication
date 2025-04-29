package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.BestsellerItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.GioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.ThongTinMonAnActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BestSellerAdapter(
    private val monAnList: List<MonAn>,
    private val context: Context,
    private val idNguoiDung: Int // Thêm idNguoiDung
) : RecyclerView.Adapter<BestSellerAdapter.PopularViewHolder>() {

    private val appDatabase = AppDatabase.getDatabase(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = BestsellerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val monAn = monAnList[position]
        holder.bind(monAn)

        holder.binding.ThemVaoGioBestSeller.setOnClickListener {
            addToCart(monAn)
        }

        holder.binding.CardViewBestSeller.setOnClickListener {
            val intent = Intent(context, ThongTinMonAnActivity::class.java).apply {
                putExtra("MonAnId", monAn.id)
                putExtra("MenuItemName", monAn.tenMon)
                putExtra("MenuItemImage", monAn.hinhAnh)
                putExtra("MenuItemDescription", monAn.moTa)
                putExtra("MenuItemIngredients", monAn.nguyenLieu)
                putExtra("MenuItemPrice", monAn.gia)
                putExtra("ID_NGUOI_DUNG", idNguoiDung)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = monAnList.size

    inner class PopularViewHolder(val binding: BestsellerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(monAn: MonAn) {
            binding.TenMonAnBestSeller.text = monAn.tenMon
            binding.GiaBestSeller.text = "${monAn.gia} VNĐ"
            if (monAn.hinhAnh != null) {
                Glide.with(context)
                    .load(File(monAn.hinhAnh))
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.imageView7)
            } else {
                binding.imageView7.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun addToCart(monAn: MonAn) {
        CoroutineScope(Dispatchers.Main).launch {
            val latestMonAn = appDatabase.appDao().getMonAnById(monAn.id) ?: return@launch
            var gioHang = appDatabase.appDao().getGioHangByNguoiDung(idNguoiDung)
            if (gioHang == null) {
                val newGioHang = GioHang(idNguoiDung = idNguoiDung)
                val gioHangId = appDatabase.appDao().insertGioHang(newGioHang)
                gioHang = GioHang(id = gioHangId.toInt(), idNguoiDung = idNguoiDung)
            }

            val existingItem = appDatabase.appDao().getChiTietGioHangItem(gioHang.id, monAn.id)
            if (existingItem != null) {
                val newSoLuong = existingItem.soLuong + 1
                if (newSoLuong <= latestMonAn.soLuong) {
                    appDatabase.appDao().updateChiTietGioHang(existingItem.copy(soLuong = newSoLuong))
                    Toast.makeText(context, "${monAn.tenMon} đã được cập nhật trong giỏ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Hết hàng!", Toast.LENGTH_SHORT).show()
                }
            } else if (latestMonAn.soLuong >= 1) {
                val chiTietGioHang = ChiTietGioHang(idGioHang = gioHang.id, idMonAn = monAn.id, soLuong = 1)
                appDatabase.appDao().insertChiTietGioHang(chiTietGioHang)
                Toast.makeText(context, "${monAn.tenMon} đã được thêm vào giỏ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Hết hàng!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}