package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var userId: Int = -1 // Lưu userId từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy userId từ Intent được truyền từ DangNhapActivity
        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            // Nếu không có userId hợp lệ, có thể quay lại màn hình đăng nhập hoặc xử lý lỗi
            finish()
            return
        }

        // Thiết lập NavController và BottomNavigationView
        val navController: NavController = findNavController(R.id.fragmentContainerView2)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Truyền userId đến Fragment ban đầu (TrangChuFragment)
        val bundle = Bundle().apply {
            putInt("USER_ID", userId)
        }
        navController.setGraph(R.navigation.navigation, bundle)

        // Sự kiện click cho nút thông báo
        binding.notificationButton.setOnClickListener {
            val bottomSheetDialog = ThongBao_Bottom_Fragment()
            bottomSheetDialog.show(supportFragmentManager, "ThongBao")
        }
    }

    // Phương thức để các Fragment có thể lấy userId nếu cần
    fun getUserId(): Int = userId
}