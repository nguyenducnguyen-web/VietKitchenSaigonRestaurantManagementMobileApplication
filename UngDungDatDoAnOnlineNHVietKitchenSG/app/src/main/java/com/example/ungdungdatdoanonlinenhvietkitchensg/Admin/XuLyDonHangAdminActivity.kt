package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityXuLyDonHangAdminBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.XuLyDonHangAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class XuLyDonHangAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityXuLyDonHangAdminBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var adapter: XuLyDonHangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXuLyDonHangAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getDatabase(this)
        adapter = XuLyDonHangAdapter(emptyList(), this) { loadPendingOrders() }
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingOrderRecyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            finish()
        }

        loadPendingOrders()
    }

    private fun loadPendingOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            val donHangList = appDatabase.appDao().getPendingDonHang()
            adapter.updateData(donHangList)
        }
    }
}