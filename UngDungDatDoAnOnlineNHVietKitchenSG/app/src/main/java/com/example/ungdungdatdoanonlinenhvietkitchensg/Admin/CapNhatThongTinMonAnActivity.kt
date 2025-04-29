package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityCapNhatThongTinMonAnBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CapNhatThongTinMonAnActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCapNhatThongTinMonAnBinding
    private lateinit var appDatabase: AppDatabase
    private var monAnId: Int = -1
    private var currentMonAn: MonAn? = null // Lưu trữ món ăn hiện tại để sử dụng sau

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapNhatThongTinMonAnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)
        monAnId = intent.getIntExtra("MON_AN_ID", -1)

        binding.backButton.setOnClickListener {
            finish()
        }

        loadMonAnData()

        binding.btnCapNhatThongTinMonAn.setOnClickListener {
            updateMonAn()
        }
    }

    private fun loadMonAnData() {
        CoroutineScope(Dispatchers.Main).launch {
            val monAn = appDatabase.appDao().getMonAnById(monAnId)
            if (monAn != null) {
                currentMonAn = monAn // Lưu trữ món ăn để dùng trong updateMonAn
                binding.enterFoodName.setText(monAn.tenMon)
                binding.enterFoodPrice.setText(monAn.gia.toString())
                Glide.with(this@CapNhatThongTinMonAnActivity).load(monAn.hinhAnh).into(binding.selectedImage)
                binding.edtSoLuong.setText(monAn.soLuong.toString())
                binding.description.setText(monAn.moTa)
                binding.edtNguyenLieu.setText(monAn.nguyenLieu)
            } else {
                Toast.makeText(this@CapNhatThongTinMonAnActivity, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateMonAn() {
        val tenMon = binding.enterFoodName.text.toString()
        val gia = binding.enterFoodPrice.text.toString().toDoubleOrNull()
        val soLuong = binding.edtSoLuong.text.toString().toIntOrNull()
        val moTa = binding.description.text.toString()
        val nguyenLieu = binding.edtNguyenLieu.text.toString()

        if (tenMon.isEmpty() || gia == null || soLuong == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val updatedMonAn = MonAn(
                id = monAnId,
                tenMon = tenMon,
                gia = gia,
                hinhAnh = currentMonAn?.hinhAnh,
                moTa = moTa,
                soLuong = soLuong,
                nguyenLieu = nguyenLieu
            )

            appDatabase.appDao().updateMonAn(updatedMonAn)
            Toast.makeText(this@CapNhatThongTinMonAnActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}