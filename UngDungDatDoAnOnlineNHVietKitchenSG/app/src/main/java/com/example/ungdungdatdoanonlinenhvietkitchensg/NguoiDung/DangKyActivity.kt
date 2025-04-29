package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDangKyBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.NguoiDung
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DangKyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangKyBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        binding.btnDangKy.setOnClickListener {
            val hoTen = binding.edtHoVaTen.text.toString()
            val email = binding.edtEmailOrSDT.text.toString()
            val matKhau = binding.edtMatKhau.text.toString()

            if (hoTen.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else if (!isValidGmail(email)) {
                Toast.makeText(this, "Email không đúng định dạng Gmail (@gmail.com)", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    // Kiểm tra xem email đã tồn tại chưa
                    val emailExists = database.appDao().isEmailExists(email) > 0
                    if (emailExists) {
                        Toast.makeText(this@DangKyActivity, "Email đã được đăng ký, vui lòng dùng email khác", Toast.LENGTH_SHORT).show()
                    } else {
                        val newUser = NguoiDung(
                            hoTen = hoTen,
                            email = email,
                            matKhau = matKhau,
                            diaChi = null,
                            soDienThoai = null
                        )
                        database.appDao().insertNguoiDung(newUser)
                        Toast.makeText(this@DangKyActivity, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@DangKyActivity, DangNhapActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        binding.btnDangNhap.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }
    }

    // Hàm kiểm tra định dạng email Gmail
    private fun isValidGmail(email: String): Boolean {
        val gmailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$"
        return email.matches(gmailPattern.toRegex())
    }
}