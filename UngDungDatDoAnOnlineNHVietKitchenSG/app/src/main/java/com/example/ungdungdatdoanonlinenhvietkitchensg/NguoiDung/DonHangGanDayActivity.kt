package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDonHangGanDayBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.DonHang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DonHangGanDayActivity : AppCompatActivity() {
    private val binding: ActivityDonHangGanDayBinding by lazy {
        ActivityDonHangGanDayBinding.inflate(layoutInflater)
    }
    private lateinit var appDatabase: AppDatabase
    private lateinit var adapter: DonHangAdapter
    private var idNguoiDung: Int = -1 // Lấy từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Lấy idNguoiDung từ Intent
        idNguoiDung = intent.getIntExtra("ID_NGUOI_DUNG", -1)
        if (idNguoiDung == -1) {
            finish() // Thoát nếu không có idNguoiDung hợp lệ
            return
        }

        appDatabase = AppDatabase.getDatabase(this)
        adapter = DonHangAdapter(emptyList()) { loadRecentOrders() }
        binding.RecyclerViewDonHangGanDay.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewDonHangGanDay.adapter = adapter

        binding.btnTroVe.setOnClickListener {
            finish()
        }

        binding.btnChuyenSangTrangDonBiHuy.setOnClickListener {
            val intent = Intent(this, DonHangBiHuyActivity::class.java).apply {
                putExtra("ID_NGUOI_DUNG", idNguoiDung) // Truyền idNguoiDung sang DonHangBiHuyActivity
            }
            startActivity(intent)
        }

        loadRecentOrders()
    }

    private fun loadRecentOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            val donHangList = appDatabase.appDao().getRecentDonHangByNguoiDung(idNguoiDung)
            adapter.updateData(donHangList)
        }
    }
}