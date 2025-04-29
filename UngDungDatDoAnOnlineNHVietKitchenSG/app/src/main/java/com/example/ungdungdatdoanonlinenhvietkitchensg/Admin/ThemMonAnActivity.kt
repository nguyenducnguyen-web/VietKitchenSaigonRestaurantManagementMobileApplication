package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityThemMonAnBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ThemMonAnActivity : AppCompatActivity() {
    private val binding: ActivityThemMonAnBinding by lazy {
        ActivityThemMonAnBinding.inflate(layoutInflater)
    }
    private lateinit var appDatabase: AppDatabase
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(this)

        // Chọn hình ảnh
        binding.selectImage.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Nút quay lại
        binding.backButton.setOnClickListener {
            finish()
        }

        // Nút thêm món ăn
        binding.AddItmeButton.setOnClickListener {
            addMonAnToDatabase()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.selectedImage.setImageURI(uri)
            imageUri = uri
            imagePath = saveImageToInternalStorage(uri) // Lưu ảnh và lấy đường dẫn
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileName = "monan_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath // Trả về đường dẫn tuyệt đối của file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addMonAnToDatabase() {
        val tenMon = binding.enterFoodName.text.toString().trim()
        val giaStr = binding.enterFoodPrice.text.toString().trim()
        val soLuongStr = binding.edtSoLuong.text.toString().trim()
        val moTa = binding.description.text.toString().trim()
        val nguyenLieu = binding.edtNguyenLieu.text.toString().trim()

        if (tenMon.isEmpty() || giaStr.isEmpty() || soLuongStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        val gia = giaStr.toDoubleOrNull()
        val soLuong = soLuongStr.toIntOrNull()

        if (gia == null || soLuong == null) {
            Toast.makeText(this, "Giá và số lượng phải là số hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val monAn = MonAn(
            tenMon = tenMon,
            gia = gia,
            hinhAnh = imagePath, // Lưu đường dẫn file thay vì URI
            moTa = moTa,
            soLuong = soLuong,
            nguyenLieu = nguyenLieu
        )

        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.appDao().insertMonAn(monAn)
            Toast.makeText(this@ThemMonAnActivity, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show()
            finish() // Quay lại AdminMainActivity
        }
    }
}