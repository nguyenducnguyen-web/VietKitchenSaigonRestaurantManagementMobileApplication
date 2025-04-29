package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityThongTinMonAnBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.GioHang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ThongTinMonAnActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThongTinMonAnBinding
    private lateinit var appDatabase: AppDatabase
    private var idNguoiDung: Int = -1 // Lấy từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThongTinMonAnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)
        idNguoiDung = intent.getIntExtra("ID_NGUOI_DUNG", -1)
        if (idNguoiDung == -1) {
            finish() // Thoát nếu không có idNguoiDung
            return
        }

        val monAnId = intent.getIntExtra("MonAnId", -1)
        val foodName = intent.getStringExtra("MenuItemName")
        val foodImage = intent.getStringExtra("MenuItemImage")
        val description = intent.getStringExtra("MenuItemDescription")
        val ingredients = intent.getStringExtra("MenuItemIngredients")
        val price = intent.getDoubleExtra("MenuItemPrice", 0.0)

        binding.detailFoodName.text = foodName
        binding.DescriptionTextView.text = description ?: "Không có mô tả"
        binding.IngredientsTextView.text = ingredients ?: "Không có nguyên liệu"
        if (foodImage != null) {
            Glide.with(this).load(File(foodImage)).into(binding.DetailFoodImage)
        }

        binding.backButton.setOnClickListener { finish() }

        binding.buttonThemVaoGio.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val latestMonAn = appDatabase.appDao().getMonAnById(monAnId) ?: return@launch
                var gioHang = appDatabase.appDao().getGioHangByNguoiDung(idNguoiDung)
                if (gioHang == null) {
                    val newGioHang = GioHang(idNguoiDung = idNguoiDung)
                    val gioHangId = appDatabase.appDao().insertGioHang(newGioHang)
                    gioHang = GioHang(id = gioHangId.toInt(), idNguoiDung = idNguoiDung)
                }

                val existingItem = appDatabase.appDao().getChiTietGioHangItem(gioHang.id, monAnId)
                if (existingItem != null) {
                    val newSoLuong = existingItem.soLuong + 1
                    if (newSoLuong <= latestMonAn.soLuong) {
                        appDatabase.appDao().updateChiTietGioHang(existingItem.copy(soLuong = newSoLuong))
                        Toast.makeText(this@ThongTinMonAnActivity, "${latestMonAn.tenMon} đã được cập nhật trong giỏ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ThongTinMonAnActivity, "Hết hàng!", Toast.LENGTH_SHORT).show()
                    }
                } else if (latestMonAn.soLuong >= 1) {
                    val chiTietGioHang = ChiTietGioHang(idGioHang = gioHang.id, idMonAn = monAnId, soLuong = 1)
                    appDatabase.appDao().insertChiTietGioHang(chiTietGioHang)
                    Toast.makeText(this@ThongTinMonAnActivity, "${latestMonAn.tenMon} đã được thêm vào giỏ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ThongTinMonAnActivity, "Hết hàng!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}