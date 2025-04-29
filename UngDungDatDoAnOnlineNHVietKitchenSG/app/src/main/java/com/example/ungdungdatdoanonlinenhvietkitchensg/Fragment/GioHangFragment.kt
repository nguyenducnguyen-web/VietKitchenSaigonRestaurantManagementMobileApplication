package com.example.ungdungdatdoanonlinenhvietkitchensg.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentGioHangBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.GioHangAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.ThanhToanActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.ChiTietGioHang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GioHangFragment : Fragment() {
    private var _binding: FragmentGioHangBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GioHangAdapter
    private lateinit var appDatabase: AppDatabase
    private var userId: Int = -1
    private val chiTietGioHangItems = mutableListOf<ChiTietGioHang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        userId = arguments?.getInt("USER_ID", -1) ?: requireActivity().intent.getIntExtra("USER_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGioHangBinding.inflate(inflater, container, false)

        adapter = GioHangAdapter(chiTietGioHangItems, appDatabase)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        loadCartItems()

        binding.proceedButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val gioHang = appDatabase.appDao().getGioHangByNguoiDung(userId)
                if (gioHang == null) {
                    Log.e("GioHangFragment", "Không tìm thấy giỏ hàng cho userId: $userId")
                    return@launch
                }

                val chiTietList = if (chiTietGioHangItems.isEmpty()) {
                    appDatabase.appDao().getChiTietGioHangByGioHangLive(gioHang.id).value ?: emptyList()
                } else {
                    chiTietGioHangItems
                }

                if (chiTietList.isEmpty()) {
                    Log.e("GioHangFragment", "Danh sách ChiTietGioHang rỗng khi nhấn Proceed!")
                    return@launch
                }

                var total = 0.0
                chiTietList.forEach { chiTiet ->
                    val monAn = appDatabase.appDao().getMonAnById(chiTiet.idMonAn)
                    if (monAn != null) {
                        total += monAn.gia * chiTiet.soLuong
                    }
                }

                val intent = Intent(requireContext(), ThanhToanActivity::class.java).apply {
                    putExtra("CHI_TIET_GIO_HANG_LIST", ArrayList(chiTietList))
                    putExtra("ID_NGUOI_DUNG", userId)
                    putExtra("TOTAL_AMOUNT", total)
                }
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.Main).launch {
            val gioHang = appDatabase.appDao().getGioHangByNguoiDung(userId)
            if (gioHang != null) {
                appDatabase.appDao().getChiTietGioHangByGioHangLive(gioHang.id).observe(viewLifecycleOwner, Observer { items ->
                    chiTietGioHangItems.clear()
                    chiTietGioHangItems.addAll(items)
                    adapter.notifyDataSetChanged()
                })
            } else {
                chiTietGioHangItems.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}