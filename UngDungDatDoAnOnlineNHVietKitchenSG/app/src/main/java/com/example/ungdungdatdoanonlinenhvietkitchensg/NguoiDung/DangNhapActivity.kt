package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.Admin.DangNhapAdminActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityDangNhapBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DangNhapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangNhapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        binding.btnDangNhap.setOnClickListener {
            val email = binding.edtEmailOrSDT.text.toString()
            val matKhau = binding.edtMatKhau.text.toString()

            if (email.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val user = database.appDao().loginNguoiDung(email, matKhau)
                    if (user != null) {
                        Toast.makeText(this@DangNhapActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@DangNhapActivity, MainActivity::class.java)
                        intent.putExtra("USER_ID", user.id) // Truyền id của người dùng
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@DangNhapActivity, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnDangNhapAdmin.setOnClickListener {
            val intent = Intent(this, DangNhapAdminActivity::class.java)
            startActivity(intent)
        }

        binding.btnDangKy.setOnClickListener {
            val intent = Intent(this, DangKyActivity::class.java)
            startActivity(intent)
        }
    }
}