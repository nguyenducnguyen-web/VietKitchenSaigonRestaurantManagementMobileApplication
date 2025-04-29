package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.CartItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class GioHangAdapter(
    private val chiTietGioHangItems: MutableList<ChiTietGioHang>,
    private val appDatabase: AppDatabase
) : RecyclerView.Adapter<GioHangAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = chiTietGioHangItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var monAn: MonAn? = null

        fun bind(position: Int) {
            val chiTietGioHang = chiTietGioHangItems[position]
            CoroutineScope(Dispatchers.Main).launch {
                monAn = appDatabase.appDao().getMonAnById(chiTietGioHang.idMonAn)
                monAn?.let {
                    binding.apply {
                        foodNameTextView.text = it.tenMon
                        priceTextView.text = "${it.gia} đ"
                        quantityTextView.text = chiTietGioHang.soLuong.toString()
                        if (it.hinhAnh != null) {
                            Glide.with(binding.root.context)
                                .load(File(it.hinhAnh))
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(foodImageView)
                        } else {
                            foodImageView.setImageResource(android.R.drawable.ic_menu_gallery)
                        }

                        minusButton.isEnabled = chiTietGioHang.soLuong > 1
                        plusButton.isEnabled = chiTietGioHang.soLuong < it.soLuong

                        minusButton.setOnClickListener { decreaseQuantity(position) }
                        plusButton.setOnClickListener { increaseQuantity(position) }
                        deleteButton.setOnClickListener { deleteItem(chiTietGioHang) }
                    }
                }
            }
        }

        private fun increaseQuantity(position: Int) {
            val chiTietGioHang = chiTietGioHangItems[position]
            monAn?.let { mon ->
                if (chiTietGioHang.soLuong < mon.soLuong) {
                    val newChiTietGioHang = chiTietGioHang.copy(soLuong = chiTietGioHang.soLuong + 1)
                    CoroutineScope(Dispatchers.Main).launch {
                        appDatabase.appDao().updateChiTietGioHang(newChiTietGioHang)
                        chiTietGioHangItems[position] = newChiTietGioHang
                        notifyItemChanged(position)
                    }
                } else {
                    Toast.makeText(binding.root.context, "Số lượng vượt quá tồn kho!", Toast.LENGTH_SHORT).show()
                    binding.plusButton.isEnabled = false
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            val chiTietGioHang = chiTietGioHangItems[position]
            if (chiTietGioHang.soLuong > 1) {
                val newChiTietGioHang = chiTietGioHang.copy(soLuong = chiTietGioHang.soLuong - 1)
                CoroutineScope(Dispatchers.Main).launch {
                    appDatabase.appDao().updateChiTietGioHang(newChiTietGioHang)
                    chiTietGioHangItems[position] = newChiTietGioHang
                    notifyItemChanged(position)
                }
            }
            binding.minusButton.isEnabled = chiTietGioHang.soLuong > 1
        }

        private fun deleteItem(chiTietGioHang: ChiTietGioHang) {
            CoroutineScope(Dispatchers.Main).launch {
                appDatabase.appDao().deleteChiTietGioHang(chiTietGioHang)
                // Không xóa trực tiếp từ danh sách, để LiveData cập nhật
                notifyDataSetChanged()
            }
        }
    }
}