package com.example.ungdungdatdoanonlinenhvietkitchensg.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.DatBanActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.LienHeActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.MenuAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentTimKiemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.MonAn

class TimKiemFragment : Fragment() {
    private lateinit var binding: FragmentTimKiemBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var appDatabase: AppDatabase
    private val originalMenuItems: MutableList<MonAn> = mutableListOf()
    private val filteredMenuItems: MutableList<MonAn> = mutableListOf()
    private var idNguoiDung: Int = -1 // Lấy từ arguments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        // Lấy idNguoiDung từ arguments hoặc MainActivity
        idNguoiDung = arguments?.getInt("USER_ID", -1) ?: requireActivity().intent.getIntExtra("USER_ID", -1)
        if (idNguoiDung == -1) {
            // Xử lý lỗi nếu cần
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimKiemBinding.inflate(inflater, container, false)

        // Khởi tạo adapter với idNguoiDung
        filteredMenuItems.clear()
        adapter = MenuAdapter(filteredMenuItems, requireContext(), idNguoiDung)
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        // Quan sát LiveData để cập nhật danh sách gốc
        appDatabase.appDao().getAllMonAnLive().observe(viewLifecycleOwner, Observer { items ->
            originalMenuItems.clear()
            originalMenuItems.addAll(items)
            filteredMenuItems.clear()
            filteredMenuItems.addAll(items)
            adapter.notifyDataSetChanged()
        })

        binding.btnDatBan.setOnClickListener {
            val intent = Intent(requireContext(), DatBanActivity::class.java)
            startActivity(intent)
        }

        // Thiết lập SearchView
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(newText ?: "")
                return true
            }
        })

        return binding.root
    }

    private fun filterMenuItems(query: String) {
        filteredMenuItems.clear()
        if (query.isEmpty()) {
            filteredMenuItems.addAll(originalMenuItems) // Khôi phục danh sách gốc khi query rỗng
        } else {
            filteredMenuItems.addAll(originalMenuItems.filter {
                it.tenMon.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }
}