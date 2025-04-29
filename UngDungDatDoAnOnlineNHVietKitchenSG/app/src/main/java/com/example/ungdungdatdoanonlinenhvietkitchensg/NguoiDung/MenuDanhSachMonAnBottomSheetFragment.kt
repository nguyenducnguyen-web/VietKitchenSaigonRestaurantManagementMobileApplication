package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.MenuAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentMenuDanhSachMonAnBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase

class MenuDanhSachMonAnBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuDanhSachMonAnBottomSheetBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var appDatabase: AppDatabase
    private var idNguoiDung: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        idNguoiDung = arguments?.getInt("USER_ID", -1) ?: requireActivity().intent.getIntExtra("USER_ID", -1)
        if (idNguoiDung == -1) {
            dismiss()
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuDanhSachMonAnBottomSheetBinding.inflate(inflater, container, false)
        binding.buttonBack.setOnClickListener { dismiss() }

        adapter = MenuAdapter(emptyList(), requireContext(), idNguoiDung)
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        appDatabase.appDao().getAllMonAnLive().observe(viewLifecycleOwner) { items ->
            adapter = MenuAdapter(items, requireContext(), idNguoiDung)
            binding.menuRecyclerView.adapter = adapter
        }

        return binding.root
    }
}