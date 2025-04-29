package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDangNhapAdminBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.DangNhapActivity

class DangNhapAdminActivity : AppCompatActivity() {
    private val binding: ActivityDangNhapAdminBinding by lazy {
        ActivityDangNhapAdminBinding.inflate(layoutInflater)
    }

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(this)

        binding.btnDangNhap.setOnClickListener {
            val email = binding.edtEmailOrSDT.text.toString().trim()
            val matKhau = binding.edtMatKhau.text.toString().trim()

            if (email.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show()
            } else {
                // Kiểm tra đăng nhập trong coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    val admin = appDatabase.appDao().loginAdmin(email, matKhau)
                    if (admin != null) {
                        // Đăng nhập thành công
                        Toast.makeText(this@DangNhapAdminActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@DangNhapAdminActivity, AdminMainActivity::class.java)
                        startActivity(intent)
                        finish() // Đóng activity đăng nhập sau khi thành công
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(this@DangNhapAdminActivity, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}