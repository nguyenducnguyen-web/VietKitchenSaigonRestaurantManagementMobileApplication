package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.NguoiDungAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityQuanLyNguoiDungBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.NguoiDung
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuanLyNguoiDungActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuanLyNguoiDungBinding
    private lateinit var adapter: NguoiDungAdapter
    private lateinit var appDatabase: AppDatabase
    private var originalNguoiDungList: List<NguoiDung> = emptyList()
    private var nguoiDungWithStats: List<NguoiDungWithStats> = emptyList()

    data class NguoiDungWithStats(
        val nguoiDung: NguoiDung,
        val tongTienKhachChi: Double,
        val soLuongDonDaDat: Int,
        val soLuongDonDaHuy: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuanLyNguoiDungBinding.inflate(layoutInflater)
        setContentView(binding.root)


        appDatabase = AppDatabase.getDatabase(this)
        adapter = NguoiDungAdapter(emptyList(), appDatabase, supportFragmentManager)
        binding.RecyclerViewQuanLyNguoiDung.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewQuanLyNguoiDung.adapter = adapter

        binding.backButton2.setOnClickListener { finish() }

        // Quan sát danh sách người dùng và tính toán thống kê
        appDatabase.appDao().getAllNguoiDung().observe(this) { nguoiDungList ->
            originalNguoiDungList = nguoiDungList
            CoroutineScope(Dispatchers.Main).launch {
                nguoiDungWithStats = withContext(Dispatchers.IO) {
                    nguoiDungList.map { nguoiDung ->
                        val tongTien = appDatabase.appDao().getTongTienKhachChi(nguoiDung.id) ?: 0.0
                        val soLuongDonDaDat = appDatabase.appDao().getSoLuongDonDaDat(nguoiDung.id)
                        val soLuongDonDaHuy = appDatabase.appDao().getSoLuongDonDaHuy(nguoiDung.id)
                        NguoiDungWithStats(nguoiDung, tongTien, soLuongDonDaDat, soLuongDonDaHuy)
                    }
                }
                adapter.updateData(originalNguoiDungList) // Ban đầu hiển thị danh sách gốc
            }
        }

        // Tìm kiếm theo số điện thoại
        binding.searchDonHangBangTenKhach2.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterNguoiDung(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNguoiDung(newText ?: "")
                return true
            }
        })

        // Thiết lập Spinner
        val sortOptions = listOf(
            "Sắp xếp theo số lần huỷ đơn tăng dần",
            "Sắp xếp theo số lần huỷ đơn giảm dần",
            "Sắp xếp theo số lượng đơn đã đặt tăng dần",
            "Sắp xếp theo số lượng đơn đã đặt giảm dần",
            "Sắp xếp theo tổng chi của khách tăng dần",
            "Sắp xếp theo tổng chi của khách giảm dần"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhanLoai2.adapter = spinnerAdapter

        binding.spinnerPhanLoai2.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                sortNguoiDung(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun filterNguoiDung(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalNguoiDungList
        } else {
            originalNguoiDungList.filter { it.soDienThoai?.contains(query, ignoreCase = true) == true }
        }
        adapter.updateData(filteredList)
    }

    private fun sortNguoiDung(sortOption: Int) {
        val sortedList = when (sortOption) {
            0 -> nguoiDungWithStats.sortedBy { it.soLuongDonDaHuy }.map { it.nguoiDung } // Tăng dần số lần huỷ
            1 -> nguoiDungWithStats.sortedByDescending { it.soLuongDonDaHuy }.map { it.nguoiDung } // Giảm dần số lần huỷ
            2 -> nguoiDungWithStats.sortedBy { it.soLuongDonDaDat }.map { it.nguoiDung } // Tăng dần số đơn đặt
            3 -> nguoiDungWithStats.sortedByDescending { it.soLuongDonDaDat }.map { it.nguoiDung } // Giảm dần số đơn đặt
            4 -> nguoiDungWithStats.sortedBy { it.tongTienKhachChi }.map { it.nguoiDung } // Tăng dần tổng chi
            5 -> nguoiDungWithStats.sortedByDescending { it.tongTienKhachChi }.map { it.nguoiDung } // Giảm dần tổng chi
            else -> originalNguoiDungList
        }
        adapter.updateData(sortedList)
    }
}