package com.example.ungdungdatdoanonlinenhvietkitchensg.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.adapter.BestSellerAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentTrangChuBinding
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.MenuDanhSachMonAnBottomSheetFragment
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase

class TrangChuFragment : Fragment() {
    private lateinit var binding: FragmentTrangChuBinding
    private lateinit var adapter: BestSellerAdapter
    private lateinit var appDatabase: AppDatabase
    private var userId: Int = -1 // Thêm userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        userId = arguments?.getInt("USER_ID", -1) ?: requireActivity().intent.getIntExtra("USER_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrangChuBinding.inflate(inflater, container, false)
        binding.viewAllMenu.setOnClickListener {
            val bottomSheet = MenuDanhSachMonAnBottomSheetFragment()
            val bundle = Bundle().apply {
                putInt("USER_ID", userId)
            }
            bottomSheet.arguments = bundle
            bottomSheet.show(parentFragmentManager, "MenuDanhSachMonAn")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = listOf(
            SlideModel(R.drawable.img_16, ScaleTypes.FIT),
            SlideModel(R.drawable.img_17, ScaleTypes.FIT),
            SlideModel(R.drawable.img_15, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(imageList)
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                Toast.makeText(requireContext(), "Bạn đã chọn hình thứ $position", Toast.LENGTH_SHORT).show()
            }
        })

        adapter = BestSellerAdapter(emptyList(), requireContext(), userId) // Truyền userId
        binding.BestSellerRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.BestSellerRecycleView.adapter = adapter

        appDatabase.appDao().getTopSellingMonAn().observe(viewLifecycleOwner, Observer { monAnList ->
            adapter = BestSellerAdapter(monAnList, requireContext(), userId) // Truyền userId
            binding.BestSellerRecycleView.adapter = adapter
        })
    }
}