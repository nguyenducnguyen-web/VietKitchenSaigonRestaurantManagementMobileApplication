package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityThanhToanBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietDonHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.DonHang
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ThanhToanActivity : AppCompatActivity() {
    lateinit var binding: ActivityThanhToanBinding
    private lateinit var appDatabase: AppDatabase
    private var chiTietGioHangList: List<ChiTietGioHang> = emptyList()
    private var idNguoiDung: Int = -1
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)

        chiTietGioHangList = intent.getSerializableExtra("CHI_TIET_GIO_HANG_LIST") as? ArrayList<ChiTietGioHang> ?: emptyList()
        idNguoiDung = intent.getIntExtra("ID_NGUOI_DUNG", -1)
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        Log.d("ThanhToanActivity", "Total from Intent: $totalAmount")
        Log.d("ThanhToanActivity", "ChiTietGioHangList size: ${chiTietGioHangList.size}")
        chiTietGioHangList.forEach { chiTiet ->
            Log.d("ThanhToanActivity", "idMonAn: ${chiTiet.idMonAn}, soLuong: ${chiTiet.soLuong}")
        }

        binding.edtTongTien.setText(totalAmount.toString())

        val paymentMethods = arrayOf("Thanh toán khi nhận hàng", "Thanh toán bằng thẻ tín dụng")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhuongThucThanhToan.adapter = adapter

        binding.spinnerPhuongThucThanhToan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMethod = parent.getItemAtPosition(position).toString()
                when (selectedMethod) {
                    "Thanh toán khi nhận hàng" -> binding.creditCardFields.visibility = View.GONE
                    "Thanh toán bằng thẻ tín dụng" -> binding.creditCardFields.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerPhuongThucThanhToan.setSelection(0)
        binding.creditCardFields.visibility = View.GONE

        binding.backButton.setOnClickListener { finish() }
        binding.PlaceMyOrder.setOnClickListener { placeOrder() }

        binding.edtSoThe.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.edtSoThe.text.length != 16) {
                binding.edtSoThe.error = "Số thẻ phải đúng 16 chữ số"
            }
        }

        binding.btnChonNgay.setOnClickListener { showMonthYearPicker() }

        binding.edtCVVNo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.edtCVVNo.text.length != 3) {
                binding.edtCVVNo.error = "CVV phải đúng 3 chữ số"
            }
        }


    }

    private fun placeOrder() {
        val hoTen = binding.edtName.text.toString()
        val diaChi = binding.edtAddress.text.toString()
        val soDienThoai = binding.edtPhone.text.toString()
        val phuongThucThanhToan = binding.spinnerPhuongThucThanhToan.selectedItem.toString()
        val tenChuThe = if (phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng") binding.edtTenChuThe.text.toString() else null
        val soThe = if (phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng") binding.edtSoThe.text.toString() else null
        val ngayHetHan = if (phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng") binding.edtNgayHetHan.text.toString() else null
        val cvvNo = if (phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng") binding.edtCVVNo.text.toString() else null
        val tongTien = binding.edtTongTien.text.toString().toDoubleOrNull() ?: 0.0

        if (hoTen.isEmpty() || diaChi.isEmpty() || soDienThoai.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng" && !validateCreditCardPayment()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            // Lưu đơn hàng
            val donHang = DonHang(
                idNguoiDung = idNguoiDung,
                hoTen = hoTen,
                diaChi = diaChi,
                soDienThoai = soDienThoai,
                phuongThucThanhToan = phuongThucThanhToan,
                tenChuThe = tenChuThe,
                soThe = soThe,
                ngayHetHan = ngayHetHan,
                cvvNo = cvvNo,
                tongTien = tongTien
            )
            val donHangId = appDatabase.appDao().insertDonHang(donHang)
            Log.d("ThanhToanActivity", "Đã lưu DonHang với id: $donHangId")

            // Lưu chi tiết đơn hàng
            chiTietGioHangList.forEach { chiTiet ->
                val chiTietDonHang = ChiTietDonHang(
                    idDonHang = donHangId.toInt(),
                    idMonAn = chiTiet.idMonAn,
                    soLuong = chiTiet.soLuong
                )
                appDatabase.appDao().insertChiTietDonHang(chiTietDonHang)
                Log.d("ThanhToanActivity", "Đã lưu ChiTietDonHang: idMonAn=${chiTiet.idMonAn}, soLuong=${chiTiet.soLuong}")
            }

            // Xóa toàn bộ món ăn trong giỏ hàng
            val gioHang = appDatabase.appDao().getGioHangByNguoiDung(idNguoiDung)
            if (gioHang != null) {
                appDatabase.appDao().deleteAllChiTietGioHangByGioHang(gioHang.id)
                Log.d("ThanhToanActivity", "Đã xóa toàn bộ ChiTietGioHang cho idGioHang: ${gioHang.id}")

                val remainingItems = appDatabase.appDao().getChiTietGioHangByGioHang(gioHang.id)
                Log.d("ThanhToanActivity", "Số món còn lại trong giỏ hàng: ${remainingItems.size}")
                if (remainingItems.isNotEmpty()) {
                    Log.e("ThanhToanActivity", "Giỏ hàng vẫn còn dữ liệu sau khi xóa!")
                    remainingItems.forEach { chiTiet ->
                        Log.e("ThanhToanActivity", "Còn lại: id=${chiTiet.id}, idMonAn=${chiTiet.idMonAn}")
                    }
                }
            } else {
                Log.e("ThanhToanActivity", "Không tìm thấy giỏ hàng để xóa!")
            }

            // Hiển thị ChucMungBottomSheet và không finish()
            val bottomSheetDialog = ChucMungBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "ChucMungBottomSheet")
            // Xóa Toast và finish() để giữ nguyên màn hình

            bottomSheetDialog.setOnDismissListener {
                val intent = Intent(this@ThanhToanActivity, DonHangGanDayActivity::class.java)
                startActivity(intent)
                finish() // Đóng ThanhToanActivity
            }
        }
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, _ ->
                val selectedDate = "${selectedMonth + 1}/$selectedYear"
                binding.edtNgayHetHan.setText(selectedDate)
            },
            year,
            month,
            1
        )
        datePickerDialog.datePicker.findViewById<View>(
            resources.getIdentifier("day", "id", "android")
        )?.visibility = View.GONE
        datePickerDialog.show()
    }

    private fun validateCreditCardPayment(): Boolean {
        val cardNumber = binding.edtSoThe.text.toString()
        val expiryDate = binding.edtNgayHetHan.text.toString()
        val cvv = binding.edtCVVNo.text.toString()
        val cardHolder = binding.edtTenChuThe.text.toString()

        return when {
            cardHolder.isEmpty() -> {
                Toast.makeText(this, "Vui lòng nhập tên chủ thẻ", Toast.LENGTH_SHORT).show()
                false
            }
            cardNumber.length != 16 -> {
                Toast.makeText(this, "Số thẻ phải đúng 16 chữ số", Toast.LENGTH_SHORT).show()
                false
            }
            expiryDate.isEmpty() -> {
                Toast.makeText(this, "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show()
                false
            }
            cvv.length != 3 -> {
                Toast.makeText(this, "CVV phải đúng 3 chữ số", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                Toast.makeText(this, "Thanh toán bằng thẻ tín dụng hợp lệ", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}