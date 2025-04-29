package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemQuanlynguoidungBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.NguoiDung
import com.example.ungdungdatdoanonlinenhvietkitchensg.utils.NotificationManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class NguoiDungAdapter(
    private var nguoiDungList: List<NguoiDung>,
    private val appDatabase: AppDatabase,
    private val fragmentManager: androidx.fragment.app.FragmentManager
) : RecyclerView.Adapter<NguoiDungAdapter.NguoiDungViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NguoiDungViewHolder {
        val binding = ItemQuanlynguoidungBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NguoiDungViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NguoiDungViewHolder, position: Int) {
        val nguoiDung = nguoiDungList[position]
        holder.bind(nguoiDung)
    }

    override fun getItemCount(): Int = nguoiDungList.size

    fun updateData(newList: List<NguoiDung>) {
        nguoiDungList = newList
        notifyDataSetChanged()
    }

    inner class NguoiDungViewHolder(private val binding: ItemQuanlynguoidungBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nguoiDung: NguoiDung) {
            binding.txtHoTenKhach.text = nguoiDung.hoTen
            binding.txtSoDienThoaiKhach.text = nguoiDung.soDienThoai ?: "Chưa có"
            binding.txtDiaChiKhach.text = nguoiDung.diaChi ?: "Chưa có"

            CoroutineScope(Dispatchers.Main).launch {
                val tongTien = appDatabase.appDao().getTongTienKhachChi(nguoiDung.id) ?: 0.0
                // Định dạng số với dấu phân cách hàng nghìn
                val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                val formattedTongTien = formatter.format(tongTien)
                binding.txtTongTienKhachChi.text = "$formattedTongTien VNĐ"

                val soLuongDonDaDat = appDatabase.appDao().getSoLuongDonDaDat(nguoiDung.id)
                binding.txtSoLuongDonDaDat.text = soLuongDonDaDat.toString()

                val soLuongDonDaHuy = appDatabase.appDao().getSoLuongDonDaHuy(nguoiDung.id)
                binding.SoLuongDonDaHuy.text = soLuongDonDaHuy.toString()
            }

            binding.btnCanhBao.setOnClickListener {
                NotificationManager.addNotification(
                    "Tài khoản của bạn có thể bị cấm do số lần huỷ đơn vượt mức cho phép",
                    R.drawable.iconwarming
                )
                Toast.makeText(binding.root.context, "Đã gửi cảnh báo đến người dùng!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}