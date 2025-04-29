package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.MenuItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.ThongTinMonAnActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.GioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MenuAdapter(
    private val menuItems: List<MonAn>,
    private val requireContext: Context,
    private val idNguoiDung: Int
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val appDatabase = AppDatabase.getDatabase(requireContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val monAn = menuItems[position]
                    val intent = Intent(requireContext, ThongTinMonAnActivity::class.java).apply {
                        putExtra("MonAnId", monAn.id)
                        putExtra("MenuItemName", monAn.tenMon)
                        putExtra("MenuItemImage", monAn.hinhAnh)
                        putExtra("MenuItemDescription", monAn.moTa)
                        putExtra("MenuItemIngredients", monAn.nguyenLieu)
                        putExtra("MenuItemPrice", monAn.gia)
                        putExtra("ID_NGUOI_DUNG", idNguoiDung)
                    }
                    requireContext.startActivity(intent)
                }
            }

            binding.btnmenuThemVaoGio.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    addToCart(position)
                }
            }
        }

        fun bind(position: Int) {
            val monAn = menuItems[position]
            binding.apply {
                menuTenMonAn.text = monAn.tenMon
                menuGiaBest.text = "${monAn.gia} đ"
                if (monAn.hinhAnh != null) {
                    Glide.with(requireContext)
                        .load(File(monAn.hinhAnh))
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(menuImage)
                } else {
                    menuImage.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            }
        }

        private fun addToCart(position: Int) {
            val monAn = menuItems[position]
            CoroutineScope(Dispatchers.Main).launch {
                val latestMonAn = appDatabase.appDao().getMonAnById(monAn.id)
                if (latestMonAn == null) {
                    Toast.makeText(requireContext, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

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
                        val updatedItem = existingItem.copy(soLuong = newSoLuong)
                        appDatabase.appDao().updateChiTietGioHang(updatedItem)
                        Toast.makeText(requireContext, "${monAn.tenMon} đã được cập nhật số lượng trong giỏ hàng", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext, "Số lượng vượt quá tồn kho (${latestMonAn.soLuong})!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                } else {
                    if (latestMonAn.soLuong >= 1) {
                        val chiTietGioHang = ChiTietGioHang(
                            idGioHang = gioHang.id,
                            idMonAn = monAn.id,
                            soLuong = 1
                        )
                        appDatabase.appDao().insertChiTietGioHang(chiTietGioHang)
                        Toast.makeText(requireContext, "${monAn.tenMon} đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext, "Món ăn ${monAn.tenMon} đã hết hàng!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
                // Không điều hướng trực tiếp từ BottomSheet nữa
            }
        }
    }
}