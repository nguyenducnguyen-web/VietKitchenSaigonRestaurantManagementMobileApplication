package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatButton
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.LienHeAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.LienHe
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File

class LienHeActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var btnLoadImage: Button
    private lateinit var btnLoadFileTxt: Button
    private lateinit var btnLoadFileExcel: Button
    private lateinit var tvLoadFileTxt: TextView
    private lateinit var tvLoadFileExcel: TextView
    private lateinit var btnLuu: AppCompatButton
    private lateinit var btnXoa: AppCompatButton
    private lateinit var btnGui: AppCompatButton
    private lateinit var backButtonk: ImageButton

    private lateinit var etChuDe: TextInputEditText
    private lateinit var etNoiDung: TextInputEditText
    private var hinhAnhUri: String? = null
    private var txtUri: String? = null
    private var excelUri: String? = null
    private var selectedLienHe: LienHe? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: AppDatabase
    private lateinit var adapter: LienHeAdapter
    private val listLienHe: MutableList<LienHe> = mutableListOf()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PICK_TXT_REQUEST = 2
        private const val PICK_EXCEL_REQUEST = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lien_he)
        initializeViews()
        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun initializeViews() {
        etChuDe = findViewById(R.id.titleInput)
        etNoiDung = findViewById(R.id.contentInput)
        imageView = findViewById(R.id.imageView)
        btnLoadImage = findViewById(R.id.btnLoadImage)
        btnLoadFileTxt = findViewById(R.id.btnLoadFileTxt)
        btnLoadFileExcel = findViewById(R.id.btnLoadExcel)
        tvLoadFileTxt = findViewById(R.id.tvLoadFileTxt)
        tvLoadFileExcel = findViewById(R.id.tvLoadFileExcel)
        btnLuu = findViewById(R.id.btnLuu)
        btnXoa = findViewById(R.id.btnXoa)
        btnGui = findViewById(R.id.btnGui)
        backButtonk = findViewById(R.id.backButtonk) // Khởi tạo backButtonk
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupRecyclerView() {
        database = AppDatabase.getDatabase(applicationContext)
        adapter = LienHeAdapter(listLienHe) { lienHe ->
            selectedLienHe = lienHe
            showToast("Đã chọn: ${lienHe.chuDe}")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setupListeners() {
        btnLoadImage.setOnClickListener {
            pickFile(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PICK_IMAGE_REQUEST)
        }

        btnLoadFileTxt.setOnClickListener {
            pickFile(Intent.ACTION_GET_CONTENT, null, PICK_TXT_REQUEST, "text/plain")
        }

        btnLoadFileExcel.setOnClickListener {
            pickFile(Intent.ACTION_GET_CONTENT, null, PICK_EXCEL_REQUEST, "application/*")
        }

        btnLuu.setOnClickListener {
            luuDuLieu()
        }

        backButtonk.setOnClickListener {
            finish()
        }

        btnXoa.setOnClickListener {
            selectedLienHe?.let {
                Xoa(it)
            } ?: showToast("Vui lòng chọn một liên hệ trước khi xóa!")
        }

        btnGui.setOnClickListener {
            val chuDe = etChuDe.text.toString().trim()
            val noiDung = etNoiDung.text.toString().trim()

            if (chuDe.isEmpty() || noiDung.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ chủ đề và nội dung")
                return@setOnClickListener
            }

            Log.d("LienHeActivity", "Sending: hinhAnh=$hinhAnhUri, txt=$txtUri, excel=$excelUri")

            val intent = Intent(this, EmailActivity::class.java).apply {
                putExtra("title", chuDe)
                putExtra("content", noiDung)
                putExtra("imageUri", hinhAnhUri ?: "")
                putExtra("txtUri", txtUri ?: "")
                putExtra("excelUri", excelUri ?: "")
            }
            startActivity(intent)
        }
    }

    private fun pickFile(action: String, uri: Uri?, requestCode: Int, type: String? = null) {
        val intent = Intent(action)
        type?.let { intent.type = it }
        uri?.let { intent.setData(it) }
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            data.data?.let { uri ->
                val fileName = getFileName(uri)
                handleFileSelection(requestCode, uri, fileName)
            }
        }
    }

    private fun handleFileSelection(requestCode: Int, uri: Uri, fileName: String) {
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                hinhAnhUri = uri.toString()
                imageView.setImageURI(uri)
                imageView.setOnClickListener { openFile(uri, "image/*") }
            }
            PICK_TXT_REQUEST -> {
                txtUri = uri.toString()
                tvLoadFileTxt.text = "File TXT: $fileName"
                tvLoadFileTxt.setOnClickListener { openFile(uri, "text/plain") }
            }
            PICK_EXCEL_REQUEST -> {
                excelUri = uri.toString()
                tvLoadFileExcel.text = "File Excel: $fileName"
                tvLoadFileExcel.setOnClickListener {
                    openFile(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                }
            }
        }
    }

    private fun openFile(uri: Uri, type: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, type)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Không có ứng dụng hỗ trợ mở file này!")
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        return result ?: uri.path?.substringAfterLast("/") ?: "Unknown"
    }

    private fun Xoa(lienHe: LienHe) {
        lifecycleScope.launch {
            try {
                database.appDao().delete(lienHe)
                listLienHe.remove(lienHe)
                adapter.updateData(listLienHe)
                selectedLienHe = null
                showToast("Đã xóa liên hệ!")
            } catch (e: Exception) {
                showToast("Lỗi khi xóa: ${e.message}")
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            database.appDao().getAllLienHe().observe(this@LienHeActivity) { lienHes ->
                if (lienHes.isNullOrEmpty()) {
                    // showToast("Không có dữ liệu để hiển thị")
                } else {
                    listLienHe.clear()
                    listLienHe.addAll(lienHes)
                    adapter.updateData(lienHes)
                }
            }
        }
    }

    private fun luuDuLieu() {
        val chuDe = etChuDe.text.toString().trim()
        val noiDung = etNoiDung.text.toString().trim()

        if (chuDe.isEmpty() || noiDung.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ chủ đề và nội dung")
            return
        }

        val fileName = "$chuDe.txt"
        val file = File(getExternalFilesDir(null), fileName)

        try {
            file.writeText(noiDung)

            val newLienHe = LienHe(
                chuDe = chuDe,
                noiDung = noiDung,
                hinhAnh = hinhAnhUri,
                filetxt = txtUri,
                fileExcel = excelUri
            )

            lifecycleScope.launch {
                database.appDao().insert(newLienHe)
                listLienHe.add(newLienHe)
                adapter.updateData(listLienHe)
                resetForm()
                showToast("Lưu thành công!\nĐường dẫn: ${file.absolutePath}")
                Log.d("FileSaved", "File đã lưu tại: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            showToast("Lỗi lưu file: ${e.message}")
            Log.e("FileError", "Lỗi lưu file", e)
        }
    }

    private fun resetForm() {
        etChuDe.text?.clear()
        etNoiDung.text?.clear()
        hinhAnhUri = null
        txtUri = null
        excelUri = null
        imageView.setImageDrawable(null)
        tvLoadFileTxt.text = ""
        tvLoadFileExcel.text = ""
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}