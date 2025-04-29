package com.example.ungdungdatdoanonlinenhvietkitchensg.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.FragmentLichSuBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.MuaLaiItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ItemMonAnBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.LienHeActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung.DonHangGanDayActivity
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LichSuFragment : Fragment() {
    private var _binding: FragmentLichSuBinding? = null
    private val binding get() = _binding!!
    private lateinit var buyAgainAdapter: MuaLaiAdapter
    private lateinit var appDatabase: AppDatabase
    private var idNguoiDung: Int = -1 // Lấy từ arguments hoặc MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        // Lấy idNguoiDung từ arguments hoặc MainActivity
        idNguoiDung = arguments?.getInt("USER_ID", -1) ?: requireActivity().intent.getIntExtra("USER_ID", -1)
        if (idNguoiDung == -1) {
            // Xử lý trường hợp không có idNguoiDung hợp lệ (có thể hiển thị thông báo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLichSuBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadLatestOrder()

        binding.btnFeedBack.setOnClickListener {
            val intent = Intent(requireContext(), LienHeActivity::class.java)
            startActivity(intent)
        }
        binding.DonHangVuaDat.setOnClickListener {
            val intent = Intent(requireContext(), DonHangGanDayActivity::class.java).apply {
                putExtra("ID_NGUOI_DUNG", idNguoiDung) // Truyền idNguoiDung sang DonHangGanDayActivity
            }
            startActivity(intent)
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        CoroutineScope(Dispatchers.Main).launch {
            val completedOrders = appDatabase.appDao().getCompletedOrders(idNguoiDung)
            buyAgainAdapter = MuaLaiAdapter(completedOrders) { donHang ->
                handleBuyAgain(donHang)
            }
            binding.MuaLaiRecyclerView.adapter = buyAgainAdapter
            binding.MuaLaiRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadLatestOrder() {
        CoroutineScope(Dispatchers.Main).launch {
            val latestDonHang = appDatabase.appDao().getLatestDonHang(idNguoiDung)
            if (latestDonHang != null) {
                val chiTietList = appDatabase.appDao().getChiTietDonHangByDonHang(latestDonHang.id)
                if (chiTietList.isNotEmpty()) {
                    val latestChiTiet = chiTietList[0]
                    val monAn = appDatabase.appDao().getMonAnById(latestChiTiet.idMonAn)

                    binding.txtHoTen.text = latestDonHang.hoTen
                    binding.txtSoDienThoai.text = latestDonHang.soDienThoai
                    binding.txtPhuongThucThanhToan.text = latestDonHang.phuongThucThanhToan
                    binding.txtDiaChi.text = latestDonHang.diaChi
                    binding.txtTrangThai.text = latestDonHang.trangThai
                    binding.txtTongTien.text = "${latestDonHang.tongTien} VNĐ"
                    binding.txtNgayDatHang.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(latestChiTiet.ngayDatHang)

                    val monAnList = listOf(
                        MonAnChiTiet(monAn?.hinhAnh, monAn?.tenMon, monAn?.gia ?: 0.0, latestChiTiet.soLuong, latestChiTiet.ngayDatHang)
                    )
                    val chiTietAdapter = ChiTietMonAnAdapter(monAnList)
                    binding.rvChiTietMonAn.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvChiTietMonAn.adapter = chiTietAdapter
                }
            } else {
                binding.DonHangVuaDat.visibility = View.GONE
            }
        }
    }

    private fun handleBuyAgain(donHang: DonHang) {
        CoroutineScope(Dispatchers.Main).launch {
            val chiTietList = appDatabase.appDao().getChiTietDonHangByDonHang(donHang.id)
            var gioHang = appDatabase.appDao().getGioHangByNguoiDung(idNguoiDung)
            if (gioHang == null) {
                gioHang = GioHang(idNguoiDung = idNguoiDung)
                val gioHangId = appDatabase.appDao().insertGioHang(gioHang)
                gioHang = gioHang.copy(id = gioHangId.toInt())
            }

            chiTietList.forEach { chiTiet ->
                val existingItem = appDatabase.appDao().getChiTietGioHangByMonAn(gioHang.id, chiTiet.idMonAn)
                if (existingItem != null) {
                    val updatedItem = existingItem.copy(soLuong = existingItem.soLuong + chiTiet.soLuong)
                    appDatabase.appDao().updateChiTietGioHang(updatedItem)
                } else {
                    val chiTietGioHang = ChiTietGioHang(
                        idGioHang = gioHang.id,
                        idMonAn = chiTiet.idMonAn,
                        soLuong = chiTiet.soLuong
                    )
                    appDatabase.appDao().insertChiTietGioHang(chiTietGioHang)
                }
            }

            findNavController().navigate(R.id.action_lichSuFragment_to_gioHangFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class MonAnChiTiet(
    val hinhAnh: String?,
    val tenMon: String?,
    val gia: Double,
    val soLuong: Int,
    val ngayDatHang: Date
)

class ChiTietMonAnAdapter(private var monAnList: List<MonAnChiTiet>) :
    RecyclerView.Adapter<ChiTietMonAnAdapter.ChiTietViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChiTietViewHolder {
        val binding = ItemMonAnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChiTietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChiTietViewHolder, position: Int) {
        val monAn = monAnList[position]
        holder.bind(monAn)
    }

    override fun getItemCount(): Int = monAnList.size

    fun updateData(newList: List<MonAnChiTiet>) {
        monAnList = newList
        notifyDataSetChanged()
    }

    inner class ChiTietViewHolder(private val binding: ItemMonAnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(monAn: MonAnChiTiet) {
            Glide.with(binding.root.context).load(monAn.hinhAnh).into(binding.AnhMonAnDonHang)
            binding.TenMonAnDonHang.text = monAn.tenMon
            binding.GiaMonAnDonHang.text = "${monAn.gia} VNĐ"
            binding.txtSoLuongDat.text = "x${monAn.soLuong}"
        }
    }
}

class MuaLaiAdapter(
    private val donHangList: List<DonHang>,
    private val onBuyAgainClicked: (DonHang) -> Unit
) : RecyclerView.Adapter<MuaLaiAdapter.MuaLaiViewHolder>() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuaLaiViewHolder {
        val binding = MuaLaiItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        appDatabase = AppDatabase.getDatabase(parent.context)
        return MuaLaiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MuaLaiViewHolder, position: Int) {
        val donHang = donHangList[position]
        holder.bind(donHang)
    }

    override fun getItemCount(): Int = donHangList.size

    inner class MuaLaiViewHolder(private val binding: MuaLaiItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val chiTietAdapter = ChiTietMonAnAdapter(emptyList())

        init {
            binding.rvChiTietMonAn.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvChiTietMonAn.adapter = chiTietAdapter
        }

        fun bind(donHang: DonHang) {
            CoroutineScope(Dispatchers.Main).launch {
                val chiTietList = appDatabase.appDao().getChiTietDonHangByDonHang(donHang.id)
                val monAnList = chiTietList.map { chiTiet ->
                    val monAn = appDatabase.appDao().getMonAnById(chiTiet.idMonAn)
                    MonAnChiTiet(monAn?.hinhAnh, monAn?.tenMon, monAn?.gia ?: 0.0, chiTiet.soLuong, chiTiet.ngayDatHang)
                }
                chiTietAdapter.updateData(monAnList)

                binding.txtHoTen.text = donHang.hoTen
                binding.txtSoDienThoai.text = donHang.soDienThoai
                binding.txtPhuongThucThanhToan.text = donHang.phuongThucThanhToan
                binding.txtDiaChi.text = donHang.diaChi
                binding.txtTrangThai.text = donHang.trangThai
                binding.txtTongTien.text = "${donHang.tongTien} VNĐ"
                binding.txtNgayDatHang.text = if (chiTietList.isNotEmpty()) {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(chiTietList[0].ngayDatHang)
                } else {
                    "Không xác định"
                }

                binding.btnMuaLai.setOnClickListener {
                    onBuyAgainClicked(donHang)
                }
            }
        }
    }
}