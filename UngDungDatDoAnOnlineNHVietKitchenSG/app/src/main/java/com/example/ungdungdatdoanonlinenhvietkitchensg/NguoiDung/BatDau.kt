package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityBatDauBinding

class BatDau : AppCompatActivity() {

    private val binding: ActivityBatDauBinding by lazy {
        ActivityBatDauBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnTiepTuc.setOnClickListener {
            val intent = Intent(this, ChonDiaDiemActivity::class.java)
            startActivity(intent)
        }
    }
}