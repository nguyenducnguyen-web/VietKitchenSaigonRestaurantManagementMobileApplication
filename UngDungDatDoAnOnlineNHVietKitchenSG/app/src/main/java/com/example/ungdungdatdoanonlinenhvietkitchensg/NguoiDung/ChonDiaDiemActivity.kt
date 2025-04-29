package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityChonDiaDiemBinding

class ChonDiaDiemActivity : AppCompatActivity() {
    private val binding: ActivityChonDiaDiemBinding by lazy {
        ActivityChonDiaDiemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnTiepTuc.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }

        val locationList: Array<String> = arrayOf("Hà Nội", "TPHCM")

        val adapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.simple_list_item_1, locationList)

        val autoCompleteTextView: AutoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }
}