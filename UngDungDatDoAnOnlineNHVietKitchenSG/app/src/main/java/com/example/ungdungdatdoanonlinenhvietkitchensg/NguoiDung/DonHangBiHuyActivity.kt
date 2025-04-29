package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDonHangBiHuyBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.DonhangbihuyItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.DonHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.MonAnChiTiet // Import từ package chính
import com.example.ungdungdatdoanonlinenhvietkitchensg.ChiTietMonAnAdapter // Import từ package chính
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DonHangBiHuyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonHangBiHuyBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var adapter: DonHangBiHuyAdapter
    private var idNguoiDung: Int = -1 // Lấy từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonHangBiHuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy idNguoiDung từ Intent
        idNguoiDung = intent.getIntExtra("ID_NGUOI_DUNG", -1)
        if (idNguoiDung == -1) {
            finish() // Thoát nếu không có idNguoiDung hợp lệ
            return
        }

        appDatabase = AppDatabase.getDatabase(this)
        adapter = DonHangBiHuyAdapter(emptyList())
        binding.RecyclerViewDonHangGanDay.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewDonHangGanDay.adapter = adapter

        binding.btnTroVe.setOnClickListener {
            finish()
        }

        loadCancelledOrders()
    }

    private fun loadCancelledOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            val donHangList = appDatabase.appDao().getCancelledDonHangByNguoiDung(idNguoiDung)
            adapter.updateData(donHangList)
        }
    }
}

class DonHangBiHuyAdapter(private var donHangList: List<DonHang>) :
    RecyclerView.Adapter<DonHangBiHuyAdapter.DonHangViewHolder>() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonHangViewHolder {
        val binding = DonhangbihuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class DonHangViewHolder(private val binding: DonhangbihuyItemBinding) :
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

                val ngayDatHang = if (chiTietList.isNotEmpty()) {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(chiTietList[0].ngayDatHang)
                } else {
                    "Không xác định"
                }
                binding.txtNgayDatHang.text = ngayDatHang
            }
        }
    }
}