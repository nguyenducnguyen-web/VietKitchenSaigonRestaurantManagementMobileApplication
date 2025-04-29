package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemMonAnBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.XulydonhangItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.DonHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.utils.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class XuLyDonHangAdapter(
    private var donHangList: List<DonHang>,
    private val context: Context,
    private val onOrderUpdated: () -> Unit
) : RecyclerView.Adapter<XuLyDonHangAdapter.DonHangViewHolder>() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonHangViewHolder {
        val binding = XulydonhangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        appDatabase = AppDatabase.getDatabase(parent.context)
        return DonHangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonHangViewHolder, position: Int) {
        val donHang = donHangList[position]
        holder.bind(donHang)
    }

    override fun getItemCount(): Int = donHangList.size

    fun updateData(newList: List<DonHang>) {
        donHangList = newList
        notifyDataSetChanged()
    }

    inner class DonHangViewHolder(private val binding: XulydonhangItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chiTietAdapter = ChiTietMonAnAdapter(emptyList())

        init {
            binding.rvChiTietMonAn.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvChiTietMonAn.adapter = chiTietAdapter
        }

        fun bind(donHang: DonHang) {
            CoroutineScope(Dispatchers.Main).launch {
                val chiTietList = appDatabase.appDao().getChiTietDonHangByDonHang(donHang.id)
                val monAnList = chiTietList.map { chiTiet ->
                    val monAn = appDatabase.appDao().getMonAnById(chiTiet.idMonAn)
                    MonAnChiTiet(monAn?.hinhAnh, monAn?.tenMon, monAn?.gia ?: 0.0, chiTiet.soLuong, chiTiet.ngayDatHang)
                }
                chiTietAdapter.updateData(monAnList)

                binding.txtHoTen.text = donHang.hoTen
                binding.txtSoDienThoai.text = donHang.soDienThoai
                binding.txtPhuongThucThanhToan.text = donHang.phuongThucThanhToan
                binding.txtDiaChi.text = donHang.diaChi
                binding.txtTrangThai.text = donHang.trangThai
                binding.txtTongTien.text = "${donHang.tongTien} VNĐ"
                binding.txtNgayDatHang.text = if (chiTietList.isNotEmpty()) {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(chiTietList[0].ngayDatHang)
                } else {
                    "Không xác định"
                }

                binding.btnTuChoi.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        appDatabase.appDao().updateTrangThaiDonHang(donHang.id, "Đơn hàng đã bị huỷ")
                        onOrderUpdated()
                        NotificationManager.addNotification(
                            "Đơn đặt hàng của bạn đã bị huỷ do một số vấn đề liên quan",
                            R.drawable.sad
                        )
                    }
                }

                binding.btnXacNhan.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        appDatabase.appDao().updateTrangThaiDonHang(donHang.id, "Đơn hàng đã được xác nhận")
                        onOrderUpdated()
                        NotificationManager.addNotification(
                            "Đơn đặt hàng của bạn đã được xác nhận",
                            R.drawable.xacnhan
                        )
                    }
                }

            }
        }
    }
}

data class MonAnChiTiet(
    val hinhAnh: String?,
    val tenMon: String?,
    val gia: Double,
    val soLuong: Int,
    val ngayDatHang: java.util.Date
)

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

