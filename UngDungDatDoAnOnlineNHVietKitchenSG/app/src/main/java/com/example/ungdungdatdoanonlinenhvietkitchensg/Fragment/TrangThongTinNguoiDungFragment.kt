package com.example.ungdungdatdoanonlinenhvietkitchensg.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentTrangThongTinNguoiDungBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.NguoiDung
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.DangNhapActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrangThongTinNguoiDungFragment : Fragment() {
    private lateinit var binding: FragmentTrangThongTinNguoiDungBinding
    private lateinit var appDatabase: AppDatabase
    private var isEditing = false
    private lateinit var currentNguoiDung: NguoiDung
    private var userId: Int = -1 // Lưu userId như một biến thành viên

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrangThongTinNguoiDungBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getDatabase(requireContext())

        // Lấy userId từ Activity và lưu vào biến thành viên
        userId = requireActivity().intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            loadNguoiDungInfo(userId)
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show()
        }

        binding.btnChinhSua.setOnClickListener {
            toggleEditMode()
        }

        binding.btnSave.setOnClickListener {
            if (isEditing) {
                saveNguoiDungInfo()
            }
        }

        binding.btnDangXuat.setOnClickListener {
            val intent = Intent(requireContext(), DangNhapActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

    private fun loadNguoiDungInfo(userId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val nguoiDung = appDatabase.appDao().getNguoiDungById(userId)
            if (nguoiDung != null) {
                currentNguoiDung = nguoiDung
                binding.edtTen.setText(nguoiDung.hoTen)
                binding.edtDiaChi.setText(nguoiDung.diaChi)
                binding.edtEmail.setText(nguoiDung.email)
                binding.edtDienThoai.setText(nguoiDung.soDienThoai)
                binding.edtMatKhau.setText(nguoiDung.matKhau)

                setEditTextEnabled(false)
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleEditMode() {
        isEditing = !isEditing
        if (isEditing) {
            binding.btnChinhSua.text = "Hủy"
            setEditTextEnabled(true)
            binding.edtEmail.isEnabled = false // Email không thể chỉnh sửa
            binding.btnSave.isEnabled = true
        } else {
            binding.btnChinhSua.text = "Chỉnh sửa"
            setEditTextEnabled(false)
            binding.btnSave.isEnabled = false
            if (userId != -1) {
                loadNguoiDungInfo(userId) // Truyền userId để tải lại thông tin
            }
        }
    }

    private fun setEditTextEnabled(enabled: Boolean) {
        binding.edtTen.isEnabled = enabled
        binding.edtDiaChi.isEnabled = enabled
        binding.edtDienThoai.isEnabled = enabled
        binding.edtMatKhau.isEnabled = enabled
    }

    private fun saveNguoiDungInfo() {
        val updatedNguoiDung = NguoiDung(
            id = currentNguoiDung.id, // Giữ nguyên id
            hoTen = binding.edtTen.text.toString().trim(),
            email = currentNguoiDung.email, // Email không thay đổi
            matKhau = binding.edtMatKhau.text.toString().trim(),
            diaChi = binding.edtDiaChi.text.toString().trim(),
            soDienThoai = binding.edtDienThoai.text.toString().trim()
        )

        CoroutineScope(Dispatchers.Main).launch {
            appDatabase.appDao().updateNguoiDung(updatedNguoiDung)
            Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            currentNguoiDung = updatedNguoiDung
            toggleEditMode()
        }
    }
}