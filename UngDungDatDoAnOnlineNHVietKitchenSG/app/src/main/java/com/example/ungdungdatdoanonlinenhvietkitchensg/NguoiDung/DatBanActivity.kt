package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.BanAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDatBanBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.Ban
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DatBanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatBanBinding
    private lateinit var adapter: BanAdapter
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatBanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)
        adapter = BanAdapter(
            emptyList(),
            BanAdapter.LayoutType.DAT_BAN,
            appDatabase,
            onCancelClick = { ban -> cancelBan(ban) }
        )
        binding.RecyclerViewDatBan.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewDatBan.adapter = adapter

        // Thiết lập Spinner
        val soLuongKhachOptions = arrayOf("1", "2", "3", "4", "5")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, soLuongKhachOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSoLuongKhach.adapter = spinnerAdapter

        binding.backButton21.setOnClickListener { finish() }
        binding.buttonChonNgay.setOnClickListener { showDatePicker() }
        binding.btnDatBan.setOnClickListener { saveBan() }

        appDatabase.appDao().getLatestBanLive().observe(this) { ban ->
            if (ban != null) {
                adapter.updateData(listOf(ban)) // Chuyển bản ghi thành danh sách 1 phần tử
            } else {
                adapter.updateData(emptyList()) // Nếu không có dữ liệu
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.editTextNgayDatHang.setText(dateFormat.format(selectedDate))
            },
            year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun saveBan() {
        val tenKhachDat = binding.enterTenKhach.text.toString().trim()
        val soDienThoai = binding.enterSdt.text.toString().trim()
        val soLuongKhach = binding.spinnerSoLuongKhach.selectedItem.toString().toIntOrNull() ?: 0
        val ngayDatStr = binding.editTextNgayDatHang.text.toString().trim()

        if (tenKhachDat.isEmpty() || soDienThoai.isEmpty() || soLuongKhach == 0 || ngayDatStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val ngayDat: Date
        try {
            ngayDat = dateFormat.parse(ngayDatStr) ?: return
            val currentTime = Date()
            if (ngayDat.before(currentTime)) {
                Toast.makeText(this, "Ngày đặt không được là quá khứ!", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ngày không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        val ban = Ban(
            tenKhachDat = tenKhachDat,
            soDienThoai = soDienThoai,
            soLuongKhach = soLuongKhach,
            ngayDat = ngayDat,
            trangThai = "Đang xử lý"
        )

        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.appDao().insertBan(ban)
            Toast.makeText(this@DatBanActivity, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show()
            resetForm()
        }
    }

    private fun cancelBan(ban: Ban) {
        CoroutineScope(Dispatchers.Main).launch {
            val updatedBan = ban.copy(trangThai = "Đã bị huỷ")
            appDatabase.appDao().updateBan(updatedBan)
            Toast.makeText(this@DatBanActivity, "Đã hủy đặt bàn!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetForm() {
        binding.enterTenKhach.text.clear()
        binding.enterSdt.text.clear()
        binding.spinnerSoLuongKhach.setSelection(0)
        binding.editTextNgayDatHang.text.clear()
    }
}