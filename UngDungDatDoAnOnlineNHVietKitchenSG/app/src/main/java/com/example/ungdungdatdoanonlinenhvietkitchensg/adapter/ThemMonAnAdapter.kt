package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.Admin.CapNhatThongTinMonAnActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThemMonAnAdapter(
    private var monAnList: MutableList<MonAn>, // Thay đổi từ val thành var để có thể gán lại danh sách
    private val appDatabase: AppDatabase
) : RecyclerView.Adapter<ThemMonAnAdapter.MonAnViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonAnViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonAnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonAnViewHolder, position: Int) {
        val monAn = monAnList[position]
        holder.bind(monAn)
    }

    override fun getItemCount(): Int = monAnList.size

    // Thêm hàm updateData để cập nhật danh sách món ăn
    fun updateData(newList: MutableList<MonAn>) {
        monAnList = newList
        notifyDataSetChanged()
    }

    inner class MonAnViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(monAn: MonAn) {
            Glide.with(binding.root.context).load(monAn.hinhAnh).into(binding.foodImageView)
            binding.foodNameTextView.text = monAn.tenMon
            binding.priceTextView.text = "${monAn.gia} VNĐ"
            binding.quantityTextView.text = monAn.soLuong.toString()

            binding.btnCapNhat.setOnClickListener {
                val intent = Intent(binding.root.context, CapNhatThongTinMonAnActivity::class.java).apply {
                    putExtra("MON_AN_ID", monAn.id)
                }
                binding.root.context.startActivity(intent)
            }

            binding.deleteButton.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa món ăn ${monAn.tenMon}?")
                    .setPositiveButton("Có") { _, _ ->
                        CoroutineScope(Dispatchers.Main).launch {
                            appDatabase.appDao().deleteMonAn(monAn)
                            monAnList.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        }
                    }
                    .setNegativeButton("Không", null)
                    .show()
            }
        }
    }
}