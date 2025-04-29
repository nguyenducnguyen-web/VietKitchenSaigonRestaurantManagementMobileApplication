package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityAdminMainBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import java.text.DecimalFormat

class AdminMainActivity : AppCompatActivity() {
    private val binding: ActivityAdminMainBinding by lazy {
        ActivityAdminMainBinding.inflate(layoutInflater)
    }
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(this)

        // Thiết lập các sự kiện click
        binding.addMenu.setOnClickListener {
            startActivity(Intent(this, ThemMonAnActivity::class.java))
        }
        binding.allItemMenu.setOnClickListener {
            startActivity(Intent(this, QuanLyDanhSachMonAnAllItemActivity::class.java))
        }
        binding.outForDeliveryButton1.setOnClickListener {
            startActivity(Intent(this, TrangThaiGiaoHangActivity::class.java))
        }
        binding.DonDaXacNhan1.setOnClickListener {
            startActivity(Intent(this, TrangThaiGiaoHangActivity::class.java))
        }
        binding.AdminProfile.setOnClickListener {
            startActivity(Intent(this, TrangThongTinAdminActivity::class.java))
        }
        binding.pendingOrderTextView.setOnClickListener {
            startActivity(Intent(this, XuLyDonHangAdminActivity::class.java))
        }
        binding.QuanLyDonHang1.setOnClickListener {
            startActivity(Intent(this, QuanLyDonHangActivity::class.java))
        }
        binding.btnDangXuat.setOnClickListener {
            startActivity(Intent(this, QuanLyNguoiDungActivity::class.java))
        }

        binding.btnQuanLyDatBan.setOnClickListener {
            startActivity(Intent(this, QuanLyDatBanActivity::class.java))
        }

        // Cập nhật tự động các TextView
        setupOrderStats()
    }

    private fun setupOrderStats() {
        //Số đơn hàng "Đang xử lý"
        appDatabase.appDao().getPendingOrdersCount().observe(this, Observer { count ->
            binding.txtDonChoXuLy.text = count.toString()
        })

        // Số đơn hàng "Đơn hàng đã được xác nhận"
        appDatabase.appDao().getConfirmedOrdersCount().observe(this, Observer { count ->
            binding.txtDonDaXacNhan.text = count.toString()
        })

        //Tổng doanh thu
        appDatabase.appDao().getTotalRevenue().observe(this, Observer { revenue ->
            val decimalFormat = DecimalFormat("#,### VNĐ")
            binding.txtDoanhThu.text = decimalFormat.format(revenue ?: 0.0)
        })
    }
}