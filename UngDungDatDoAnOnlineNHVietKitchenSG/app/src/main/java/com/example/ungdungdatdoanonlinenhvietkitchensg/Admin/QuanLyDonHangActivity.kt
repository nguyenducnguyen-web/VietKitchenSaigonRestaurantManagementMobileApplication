package com.example.ungdungdatdoanonlinenhvietkitchensg.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatdoanonlinenhvietkitchensg.ChiTietMonAnAdapter
import com.example.ungdungdatdoanonlinenhvietkitchensg.MonAnChiTiet
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.ActivityQuanLyDonHangBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.databinding.DonhangdagiaoItemBinding
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.DonHang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class QuanLyDonHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuanLyDonHangBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var adapter: QuanLyDonHangAdapter
    private var fullDonHangList: List<DonHang> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuanLyDonHangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        appDatabase = AppDatabase.getDatabase(this)
        adapter = QuanLyDonHangAdapter(emptyList())
        binding.RecyclerViewQuanLyDonHangDaGiao.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewQuanLyDonHangDaGiao.adapter = adapter

        // Setup Spinner
        val filterOptions = arrayOf(
            "Theo tuần", "Theo tháng", "Theo giá trị cao đến thấp", "Theo giá trị thấp đến cao",
            "Theo phương thức thanh toán bằng TTD", "Theo phương thức thanh toán bằng COD"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhanLoai.adapter = spinnerAdapter

        binding.spinnerPhanLoai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                filterOrders(filterOptions[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Setup SearchView
        binding.searchDonHangBangTenKhach.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterByName(query ?: "")
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterByName(newText ?: "")
                return true
            }
        })

        loadCompletedOrders()
    }

    private fun loadCompletedOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            fullDonHangList = appDatabase.appDao().getCompletedOrdersAll()
            adapter.updateData(fullDonHangList)
        }
    }

    private fun filterOrders(filter: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val filteredList = when (filter) {
                "Theo tuần" -> fullDonHangList.filter { donHang ->
                    val chiTiet = appDatabase.appDao().getChiTietDonHangByDonHang(donHang.id).firstOrNull()
                    chiTiet?.let { isInCurrentWeek(it.ngayDatHang) } ?: false
                }
                "Theo tháng" -> fullDonHangList.filter { donHang ->
                    val chiTiet = appDatabase.appDao().getChiTietDonHangByDonHang(donHang.id).firstOrNull()
                    chiTiet?.let { isInCurrentMonth(it.ngayDatHang) } ?: false
                }
                "Theo giá trị cao đến thấp" -> fullDonHangList.sortedByDescending { it.tongTien }
                "Theo giá trị thấp đến cao" -> fullDonHangList.sortedBy { it.tongTien }
                "Theo phương thức thanh toán bằng TTD" -> fullDonHangList.filter { it.phuongThucThanhToan == "Thanh toán bằng thẻ tín dụng" }
                "Theo phương thức thanh toán bằng COD" -> fullDonHangList.filter { it.phuongThucThanhToan == "Thanh toán khi nhận hàng" }
                else -> fullDonHangList
            }
            adapter.updateData(filteredList)
        }
    }

    private fun filterByName(query: String) {
        val filteredList = fullDonHangList.filter { it.hoTen.contains(query, ignoreCase = true) }
        adapter.updateData(filteredList)
    }

    private fun isInCurrentWeek(date: java.util.Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun isInCurrentMonth(date: java.util.Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        calendar.time = date
        return calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }
}

class QuanLyDonHangAdapter(private var donHangList: List<DonHang>) :
    RecyclerView.Adapter<QuanLyDonHangAdapter.DonHangViewHolder>() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonHangViewHolder {
        val binding = DonhangdagiaoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        appDatabase = AppDatabase.getDatabase(parent.context)
        return DonHangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonHangViewHolder, position: Int) {
        val donHang = donHangList[position]
        holder.bind(donHang)
    }

    override fun getItemCount(): Int = donHangList.size

    fun updateData(newList: List<DonHang>) {
        donHangList = newList
        notifyDataSetChanged()
    }

    inner class DonHangViewHolder(private val binding: DonhangdagiaoItemBinding) :
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
            }
        }
    }
}