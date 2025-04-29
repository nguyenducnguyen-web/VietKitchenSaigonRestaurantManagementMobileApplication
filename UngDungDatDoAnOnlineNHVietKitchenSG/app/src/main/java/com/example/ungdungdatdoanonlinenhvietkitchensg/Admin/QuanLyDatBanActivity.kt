package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.BanAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityQuanLyDatBanBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.Ban
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuanLyDatBanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuanLyDatBanBinding
    private lateinit var adapter: BanAdapter
    private lateinit var appDatabase: AppDatabase
    private var originalBanList: List<Ban> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuanLyDatBanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)
        adapter = BanAdapter(
            emptyList(),
            BanAdapter.LayoutType.QUAN_LY_DAT_BAN,
            appDatabase,
            onConfirmClick = { ban -> confirmBan(ban) },
            onRejectClick = { ban -> rejectBan(ban) }
        )
        binding.RecyclerViewQuanLyDatBanKhach.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewQuanLyDatBanKhach.adapter = adapter

        binding.backButton213.setOnClickListener { finish() }

        // Quan sát danh sách đặt bàn
        appDatabase.appDao().getAllBanLive().observe(this) { banList ->
            originalBanList = banList
            adapter.updateData(banList)
        }

        // Tìm kiếm theo số điện thoại
        binding.searchDatBanBangSDT.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBan(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBan(newText ?: "")
                return true
            }
        })

        // Thiết lập Spinner để sắp xếp
        val sortOptions = listOf(
            "Sắp xếp theo ngày đặt tăng dần",
            "Sắp xếp theo ngày đặt giảm dần",
            "Sắp xếp theo trạng thái (Đang xử lý đầu tiên)",
            "Sắp xếp theo trạng thái (Đã bị hủy đầu tiên)",
            "Sắp xếp theo ngày thêm mới nhất"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhanLoaiDatBan.adapter = spinnerAdapter

        binding.spinnerPhanLoaiDatBan.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                sortBan(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun filterBan(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalBanList
        } else {
            originalBanList.filter { it.soDienThoai.contains(query, ignoreCase = true) }
        }
        adapter.updateData(filteredList)
    }

    private fun sortBan(sortOption: Int) {
        val sortedList = when (sortOption) {
            0 -> originalBanList.sortedBy { it.ngayDat } // Ngày đặt tăng dần
            1 -> originalBanList.sortedByDescending { it.ngayDat } // Ngày đặt giảm dần
            2 -> originalBanList.sortedWith(compareBy { it.trangThai != "Đang xử lý" }) // Đang xử lý đầu tiên
            3 -> originalBanList.sortedWith(compareBy { it.trangThai != "Đã bị huỷ" }) // Đã bị hủy đầu tiên
            4 -> originalBanList.sortedByDescending { it.ngayThem } // Sắp xếp theo ngày thêm mới nhất (giảm dần)
            else -> originalBanList
        }
        adapter.updateData(sortedList)
    }

    private fun confirmBan(ban: Ban) {
        CoroutineScope(Dispatchers.Main).launch {
            val updatedBan = ban.copy(trangThai = "Yêu cầu đặt bàn đã được xác nhận")
            appDatabase.appDao().updateBan(updatedBan)
            Toast.makeText(this@QuanLyDatBanActivity, "Đã xác nhận đặt bàn!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rejectBan(ban: Ban) {
        CoroutineScope(Dispatchers.Main).launch {
            val updatedBan = ban.copy(trangThai = "Đã bị huỷ")
            appDatabase.appDao().updateBan(updatedBan)
            Toast.makeText(this@QuanLyDatBanActivity, "Đã từ chối đặt bàn!", Toast.LENGTH_SHORT).show()
        }
    }
}