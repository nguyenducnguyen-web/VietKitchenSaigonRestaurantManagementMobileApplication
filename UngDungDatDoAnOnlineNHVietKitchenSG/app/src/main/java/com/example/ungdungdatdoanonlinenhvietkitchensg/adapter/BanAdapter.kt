package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemDatbanBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemQuanlydatbanBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.Ban
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BanAdapter(
    private var banList: List<Ban>,
    private val layoutType: LayoutType,
    private val appDatabase: AppDatabase,
    private val onCancelClick: (Ban) -> Unit = {},
    private val onConfirmClick: (Ban) -> Unit = {},
    private val onRejectClick: (Ban) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class LayoutType {
        DAT_BAN, QUAN_LY_DAT_BAN
    }

    // ViewHolder cho item_datban
    inner class DatBanViewHolder(private val binding: ItemDatbanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ban: Ban) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.txtHoTenKhachDatBan2.text = ban.tenKhachDat
            binding.txtSoDienThoaiKDB2.text = ban.soDienThoai
            binding.txtSoLuongKhach2.text = ban.soLuongKhach.toString()
            binding.txtTrangThaiDatBan2.text = ban.trangThai
            binding.txtNgayDatBanCuaKhach2.text = dateFormat.format(ban.ngayDat)

            // Yêu cầu 1: Điều khiển btnHuyDatBan
            when (ban.trangThai) {
                "Đang xử lý" -> {
                    binding.btnHuyDatBan.visibility = android.view.View.VISIBLE
                    binding.btnHuyDatBan.isEnabled = true
                    binding.btnHuyDatBan.setOnClickListener {
                        onCancelClick(ban)
                    }
                }
                "Yêu cầu đặt bàn đã được xác nhận" -> {
                    binding.btnHuyDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnHuyDatBan.isEnabled = false
                }
                "Đã bị huỷ" -> {
                    binding.btnHuyDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnHuyDatBan.isEnabled = false
                }
                else -> {
                    binding.btnHuyDatBan.visibility = android.view.View.VISIBLE
                    binding.btnHuyDatBan.isEnabled = false // Không cho click nếu trạng thái khác
                }
            }
        }
    }

    // ViewHolder cho item_quanlydatban
    inner class QuanLyDatBanViewHolder(private val binding: ItemQuanlydatbanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ban: Ban) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.txtHoTenKhachDatBan.text = ban.tenKhachDat
            binding.txtSoDienThoaiKDB.text = ban.soDienThoai
            binding.txtSoLuongKhach.text = ban.soLuongKhach.toString()
            binding.txtTrangThaiDatBan.text = ban.trangThai
            binding.txtNgayDatBanCuaKhach.text = dateFormat.format(ban.ngayDat)

            // Điều khiển hiển thị nút
            when (ban.trangThai) {
                "Đang xử lý" -> {
                    binding.btnXacNhanDatBan.visibility = android.view.View.VISIBLE
                    binding.btnTuChoiDatBan.visibility = android.view.View.VISIBLE
                    binding.btnXacNhanDatBan.isEnabled = true
                    binding.btnTuChoiDatBan.isEnabled = true
                }
                "Yêu cầu đặt bàn đã được xác nhận" -> {
                    // Yêu cầu 3: Cả hai nút đều invisible
                    binding.btnXacNhanDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnTuChoiDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnXacNhanDatBan.isEnabled = false
                    binding.btnTuChoiDatBan.isEnabled = false
                }
                "Đã bị huỷ" -> {
                    // Yêu cầu 2: Cả hai nút đều invisible
                    binding.btnXacNhanDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnTuChoiDatBan.visibility = android.view.View.INVISIBLE
                    binding.btnXacNhanDatBan.isEnabled = false
                    binding.btnTuChoiDatBan.isEnabled = false
                }
                else -> {
                    binding.btnXacNhanDatBan.visibility = android.view.View.GONE
                    binding.btnTuChoiDatBan.visibility = android.view.View.GONE
                    binding.btnXacNhanDatBan.isEnabled = false
                    binding.btnTuChoiDatBan.isEnabled = false
                }
            }

            binding.btnXacNhanDatBan.setOnClickListener {
                if (ban.trangThai == "Đang xử lý") {
                    onConfirmClick(ban)
                    binding.btnTuChoiDatBan.visibility = android.view.View.INVISIBLE
                }
            }

            binding.btnTuChoiDatBan.setOnClickListener {
                if (ban.trangThai == "Đang xử lý") {
                    onRejectClick(ban)
                    binding.btnXacNhanDatBan.visibility = android.view.View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (layoutType) {
            LayoutType.DAT_BAN -> {
                val binding = ItemDatbanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DatBanViewHolder(binding)
            }
            LayoutType.QUAN_LY_DAT_BAN -> {
                val binding = ItemQuanlydatbanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                QuanLyDatBanViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ban = banList[position]
        when (holder) {
            is DatBanViewHolder -> holder.bind(ban)
            is QuanLyDatBanViewHolder -> holder.bind(ban)
        }
    }

    override fun getItemCount(): Int = banList.size

    fun updateData(newList: List<Ban>) {
        banList = newList
        notifyDataSetChanged()
    }
}