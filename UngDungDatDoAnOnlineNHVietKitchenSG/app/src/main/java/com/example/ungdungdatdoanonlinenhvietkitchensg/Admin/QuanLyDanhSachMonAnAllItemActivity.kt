package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityQuanLyDanhSachMonAnAllItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.ThemMonAnAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn

class QuanLyDanhSachMonAnAllItemActivity : AppCompatActivity() {
    private val binding: ActivityQuanLyDanhSachMonAnAllItemBinding by lazy {
        ActivityQuanLyDanhSachMonAnAllItemBinding.inflate(layoutInflater)
    }
    private lateinit var appDatabase: AppDatabase
    private lateinit var adapter: ThemMonAnAdapter
    private var originalMonAnList: List<MonAn> = emptyList() // Danh sách gốc để lọc và sắp xếp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(this)

        // Nút quay lại
        binding.backButton.setOnClickListener {
            finish()
        }

        // Thiết lập RecyclerView
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ThemMonAnAdapter(mutableListOf(), appDatabase) // Khởi tạo adapter với danh sách rỗng
        binding.MenuRecyclerView.adapter = adapter
        loadMonAnList()

        // Tìm kiếm món ăn
        binding.searchTimKiemMonAn.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMonAn(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMonAn(newText ?: "")
                return true
            }
        })

        // Thiết lập Spinner
        val sortOptions = listOf(
            "Sắp xếp theo ngày thêm mới nhất",
            "Sắp xếp theo ngày thêm cũ nhất",
            "Sắp xếp theo món ăn có giá cao đến thấp",
            "Sắp xếp theo món ăn có giá thấp đến cao",
            "Sắp xếp món ăn theo số lượng tăng dần",
            "Sắp xếp món ăn theo số lượng giảm dần"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhanLoaiMonAn.adapter = spinnerAdapter

        binding.spinnerPhanLoaiMonAn.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                sortMonAn(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun loadMonAnList() {
        appDatabase.appDao().getAllMonAnLive().observe(this) { monAnList ->
            originalMonAnList = monAnList ?: emptyList() // Cập nhật danh sách gốc
            adapter.updateData(originalMonAnList.toMutableList()) // Cập nhật adapter
        }
    }

    private fun filterMonAn(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalMonAnList
        } else {
            originalMonAnList.filter { it.tenMon.contains(query, ignoreCase = true) }
        }
        adapter.updateData(filteredList.toMutableList())
    }

    private fun sortMonAn(sortOption: Int) {
        val sortedList = when (sortOption) {
            0 -> originalMonAnList.sortedByDescending { it.ngayThem } // Ngày thêm mới nhất
            1 -> originalMonAnList.sortedBy { it.ngayThem } // Ngày thêm cũ nhất
            2 -> originalMonAnList.sortedByDescending { it.gia } // Giá cao đến thấp
            3 -> originalMonAnList.sortedBy { it.gia } // Giá thấp đến cao
            4 -> originalMonAnList.sortedBy { it.soLuong } // Số lượng tăng dần
            5 -> originalMonAnList.sortedByDescending { it.soLuong } // Số lượng giảm dần
            else -> originalMonAnList
        }
        adapter.updateData(sortedList.toMutableList())
    }
}