package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentChucMungBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChucMungBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChucMungBottomSheetBinding
    private var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChucMungBottomSheetBinding.inflate(inflater, container, false)
        binding.goHome.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            dismiss() // Đóng BottomSheet sau khi chuyển hướng
        }
        return binding.root
    }

    // Triển khai setOnDismissListener để gọi callback khi BottomSheet bị đóng
    fun setOnDismissListener(listener: () -> Unit) {
        this.onDismissListener = listener
    }

    // Ghi đè onDismiss để gọi listener khi BottomSheet bị đóng
    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    companion object {
        // Nếu cần, bạn có thể thêm logic ở đây
    }
}