package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityTrangThongTinAdminBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.Admin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrangThongTinAdminActivity : AppCompatActivity() {
    private val binding: ActivityTrangThongTinAdminBinding by lazy {
        ActivityTrangThongTinAdminBinding.inflate(layoutInflater)
    }

    private lateinit var appDatabase: AppDatabase
    private var isEditing = false
    private lateinit var currentAdmin: Admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(this)

        // Load thông tin admin từ database
        loadAdminInfo()

        // Nút Back
        binding.backButton.setOnClickListener {
            finish()
        }

        // Nút Chỉnh sửa
        binding.btnChinhSua.setOnClickListener {
            toggleEditMode()
        }

        // Nút Lưu
        binding.btnSave.setOnClickListener {
            if (isEditing) {
                saveAdminInfo()
            }
        }

        // Nút Đăng xuất
        binding.btnDangXuat.setOnClickListener {
            val intent = Intent(this, DangNhapAdminActivity::class.java)
            startActivity(intent)
            finishAffinity() // Đóng tất cả activity trước đó
        }
    }

    private fun loadAdminInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            // Giả sử lấy admin đầu tiên từ database (có thể thay đổi logic nếu có nhiều admin)
            val admin = appDatabase.appDao().loginAdmin("nhahangvietkitchensaigon@gmail.com", "admin123")
            if (admin != null) {
                currentAdmin = admin
                binding.edtName.setText(admin.hoTen)
                binding.edtDiaChi.setText(admin.diaChi)
                binding.edtEmail.setText(admin.email)
                binding.edtPhone.setText(admin.soDienThoai)
                binding.edtMatKhau.setText(admin.matKhau)

                // Vô hiệu hóa các EditText ban đầu
                setEditTextEnabled(false)
            } else {
                Toast.makeText(this@TrangThongTinAdminActivity, "Không tìm thấy thông tin admin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleEditMode() {
        isEditing = !isEditing
        if (isEditing) {
            binding.btnChinhSua.text = "Hủy"
            setEditTextEnabled(true)
            binding.edtEmail.isEnabled = false
            binding.btnSave.isEnabled = true
        } else {
            binding.btnChinhSua.text = "Chỉnh sửa"
            setEditTextEnabled(false)
            binding.btnSave.isEnabled = false
            loadAdminInfo() // Khôi phục dữ liệu ban đầu nếu hủy
        }
    }

    private fun setEditTextEnabled(enabled: Boolean) {
        binding.edtName.isEnabled = enabled
        binding.edtDiaChi.isEnabled = enabled
        binding.edtPhone.isEnabled = enabled
        binding.edtMatKhau.isEnabled = enabled
    }

    private fun saveAdminInfo() {
        val updatedAdmin = Admin(
            id = currentAdmin.id, // Giữ nguyên id
            hoTen = binding.edtName.text.toString().trim(),
            email = currentAdmin.email, // Email không thay đổi
            matKhau = binding.edtMatKhau.text.toString().trim(),
            diaChi = binding.edtDiaChi.text.toString().trim(),
            soDienThoai = binding.edtPhone.text.toString().trim()
        )

        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.appDao().updateAdmin(updatedAdmin) // Cập nhật thông tin trong database
            Toast.makeText(this@TrangThongTinAdminActivity, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            currentAdmin = updatedAdmin
            toggleEditMode() // Chuyển về chế độ xem sau khi lưu
        }
    }
}