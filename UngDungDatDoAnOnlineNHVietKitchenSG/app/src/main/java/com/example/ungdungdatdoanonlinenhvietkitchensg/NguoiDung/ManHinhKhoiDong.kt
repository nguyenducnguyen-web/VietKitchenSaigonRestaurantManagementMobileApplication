package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.R

class ManHinhKhoiDong : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_man_hinh_khoi_dong)


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BatDau::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2 giây
    }
}
