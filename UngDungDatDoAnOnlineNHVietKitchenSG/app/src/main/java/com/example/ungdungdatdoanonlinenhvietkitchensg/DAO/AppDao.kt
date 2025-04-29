package com.example.ungdungdatdoanonlinenhvietkitchensg.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.*

@Dao
interface AppDao {

    // Đếm số lượng bản ghi cho tất cả các bảng
    @Query("SELECT COUNT(*) FROM Admin")
    suspend fun getAdminCount(): Int

    @Query("SELECT COUNT(*) FROM NguoiDung")
    suspend fun getNguoiDungCount(): Int

    @Query("SELECT COUNT(*) FROM MonAn")
    suspend fun getMonAnCount(): Int

    @Query("SELECT COUNT(*) FROM GioHang")
    suspend fun getGioHangCount(): Int

    @Query("SELECT COUNT(*) FROM ChiTietGioHang")
    suspend fun getChiTietGioHangCount(): Int

    @Query("SELECT COUNT(*) FROM DonHang")
    suspend fun getDonHangCount(): Int

    @Query("SELECT COUNT(*) FROM ChiTietDonHang")
    suspend fun getChiTietDonHangCount(): Int

    @Query("SELECT COUNT(*) FROM LienHe")
    suspend fun getLienHeCount(): Int

    // NguoiDung
    @Update
    suspend fun updateNguoiDung(nguoiDung: NguoiDung)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNguoiDung(nguoiDung: NguoiDung)

    @Query("SELECT * FROM NguoiDung WHERE email = :email AND matKhau = :matKhau")
    suspend fun loginNguoiDung(email: String, matKhau: String): NguoiDung?

    @Query("SELECT * FROM NguoiDung WHERE id = :id")
    suspend fun getNguoiDungById(id: Int): NguoiDung?

    // Thêm phương thức kiểm tra email đã tồn tại
    @Query("SELECT COUNT(*) FROM NguoiDung WHERE email = :email")
    suspend fun isEmailExists(email: String): Int

    // Admin
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: Admin)

    @Update
    suspend fun updateAdmin(admin: Admin)

    @Query("SELECT * FROM Admin WHERE email = :email AND matKhau = :matKhau")
    suspend fun loginAdmin(email: String, matKhau: String): Admin?

    // MonAn
    @Update
    suspend fun updateMonAn(monAn: MonAn)

    @Delete
    suspend fun deleteMonAn(monAn: MonAn)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonAn(monAn: MonAn)

    @Query("SELECT * FROM MonAn")
    suspend fun getAllMonAn(): List<MonAn>

    @Query("SELECT * FROM MonAn")
    fun getAllMonAnLive(): LiveData<List<MonAn>>

    @Query("SELECT * FROM MonAn WHERE id = :id")
    suspend fun getMonAnById(id: Int): MonAn?

    @Query("DELETE FROM ChiTietDonHang WHERE idMonAn = :monAnId")
    suspend fun deleteChiTietDonHangByMonAnId(monAnId: Int)

    // GioHang
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGioHang(gioHang: GioHang): Long

    @Update
    suspend fun updateGioHang(gioHang: GioHang)

    @Delete
    suspend fun deleteGioHang(gioHang: GioHang)

    @Query("SELECT * FROM GioHang WHERE idNguoiDung = :idNguoiDung LIMIT 1")
    suspend fun getGioHangByNguoiDung(idNguoiDung: Int): GioHang?

    @Query("SELECT * FROM GioHang WHERE idNguoiDung = :idNguoiDung")
    fun getGioHangByNguoiDungLive(idNguoiDung: Int): LiveData<List<GioHang>>

    // ChiTietGioHang

    @Query("DELETE FROM ChiTietGioHang WHERE idGioHang = :idGioHang")
    suspend fun deleteAllChiTietGioHangByGioHang(idGioHang: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChiTietGioHang(chiTietGioHang: ChiTietGioHang)

    @Update
    suspend fun updateChiTietGioHang(chiTietGioHang: ChiTietGioHang)

    @Delete
    suspend fun deleteChiTietGioHang(chiTietGioHang: ChiTietGioHang)

    @Query("SELECT * FROM ChiTietGioHang WHERE idGioHang = :idGioHang")
    fun getChiTietGioHangByGioHangLive(idGioHang: Int): LiveData<List<ChiTietGioHang>>

    @Query("SELECT * FROM ChiTietGioHang WHERE idGioHang = :idGioHang AND idMonAn = :idMonAn LIMIT 1")
    suspend fun getChiTietGioHangItem(idGioHang: Int, idMonAn: Int): ChiTietGioHang?

    @Query("SELECT * FROM ChiTietGioHang WHERE idGioHang = :idGioHang")
    suspend fun getChiTietGioHangByGioHang(idGioHang: Int): List<ChiTietGioHang>

    // DonHang
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonHang(donHang: DonHang): Long

    @Update
    suspend fun updateDonHang(donHang: DonHang)

    //sửa
    @Query("SELECT * FROM DonHang WHERE idNguoiDung = :idNguoiDung")
    fun getDonHangByNguoiDung(idNguoiDung: Int): LiveData<List<DonHang>>


    @Query("SELECT * FROM DonHang")
    fun getAllDonHangLive(): LiveData<List<DonHang>>

    //ChiTietGioHang

    @Query("SELECT * FROM ChiTietGioHang WHERE idGioHang = :idGioHang AND idMonAn = :idMonAn LIMIT 1")
    suspend fun getChiTietGioHangByMonAn(idGioHang: Int, idMonAn: Int): ChiTietGioHang?


    //DonHang

    @Query("SELECT * FROM DonHang WHERE idNguoiDung = :idNguoiDung AND trangThai NOT IN ('Khách đã thanh toán và nhận hàng', 'Đơn hàng đã bị huỷ') ORDER BY id DESC")
    suspend fun getRecentDonHangByNguoiDung(idNguoiDung: Int): List<DonHang>

    @Query("SELECT * FROM DonHang WHERE trangThai = 'Khách đã thanh toán và nhận hàng' ORDER BY id DESC")
    suspend fun getCompletedOrdersAll(): List<DonHang>

    @Query("SELECT * FROM DonHang WHERE idNguoiDung = :idNguoiDung AND trangThai = 'Khách đã thanh toán và nhận hàng' ORDER BY id DESC")
    suspend fun getCompletedOrders(idNguoiDung: Int): List<DonHang>

    @Query("SELECT * FROM DonHang WHERE trangThai = 'Đang xử lý' ORDER BY id DESC")
    suspend fun getPendingDonHang(): List<DonHang>

    //NguoiDung
    @Query("SELECT * FROM NguoiDung")
    fun getAllNguoiDung(): LiveData<List<NguoiDung>>

    @Query("""
        SELECT SUM(tongTien) 
        FROM DonHang 
        WHERE idNguoiDung = :idNguoiDung AND trangThai = 'Khách đã thanh toán và nhận hàng'
    """)
    suspend fun getTongTienKhachChi(idNguoiDung: Int): Double?

    @Query("""
        SELECT COUNT(*) 
        FROM DonHang 
        WHERE idNguoiDung = :idNguoiDung AND trangThai = 'Khách đã thanh toán và nhận hàng'
    """)
    suspend fun getSoLuongDonDaDat(idNguoiDung: Int): Int

    @Query("""
        SELECT COUNT(*) 
        FROM DonHang 
        WHERE idNguoiDung = :idNguoiDung AND trangThai = 'Đơn hàng đã bị huỷ'
    """)
    suspend fun getSoLuongDonDaHuy(idNguoiDung: Int): Int


    // DonHang
    @Query("UPDATE DonHang SET trangThai = :trangThai WHERE id = :id")
    suspend fun updateTrangThaiDonHang(id: Int, trangThai: String)

    @Query("SELECT * FROM DonHang WHERE trangThai IN ('Đơn hàng đã được xác nhận', 'Đơn hàng đã chuẩn bị xong') ORDER BY id DESC")
    suspend fun getDeliveryDonHang(): List<DonHang>


    @Query("SELECT * FROM DonHang WHERE idNguoiDung = :idNguoiDung AND trangThai = 'Đơn hàng đã bị huỷ' ORDER BY id DESC")
    suspend fun getCancelledDonHangByNguoiDung(idNguoiDung: Int): List<DonHang>

    @Query("SELECT * FROM DonHang WHERE idNguoiDung = :idNguoiDung ORDER BY id DESC LIMIT 1")
    suspend fun getLatestDonHang(idNguoiDung: Int): DonHang?

    @Query("""
        SELECT m.*, SUM(ctdh.soLuong) as totalSold
        FROM MonAn m
        INNER JOIN ChiTietDonHang ctdh ON m.id = ctdh.idMonAn
        INNER JOIN DonHang dh ON ctdh.idDonHang = dh.id
        WHERE dh.trangThai = 'Khách đã thanh toán và nhận hàng'
        GROUP BY m.id, m.tenMon, m.gia, m.hinhAnh, m.moTa, m.soLuong, m.nguyenLieu
        ORDER BY totalSold DESC
        LIMIT 10
    """)
    fun getTopSellingMonAn(): LiveData<List<MonAn>>

    // ChiTietDonHang
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChiTietDonHang(chiTiet: ChiTietDonHang)

    @Query("SELECT * FROM ChiTietDonHang WHERE idDonHang = :idDonHang")
    suspend fun getChiTietDonHangByDonHang(idDonHang: Int): List<ChiTietDonHang>


    // LienHe
    @Insert
    suspend fun insert(lienHe: LienHe)

    @Update
    suspend fun update(lienHe: LienHe)

    // Phương thức cho LienHe

    @Delete
    suspend fun delete(lienHe: LienHe)

    @Query("SELECT * FROM lienhe ORDER BY id DESC")
    fun getAllLienHe(): LiveData<List<LienHe>>


    // Ban
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBan(ban: Ban)

    @Update
    suspend fun updateBan(ban: Ban)

    @Delete
    suspend fun deleteBan(ban: Ban)

    // Thay đổi: Lấy tất cả các bàn thay vì lọc theo idNguoiDung
    @Query("SELECT * FROM Ban ORDER BY id DESC")
    suspend fun getAllBan(): List<Ban>

    // Thay đổi: LiveData cho tất cả các bàn
    @Query("SELECT * FROM Ban ORDER BY id DESC")
    fun getAllBanLive(): LiveData<List<Ban>>

    @Query("SELECT * FROM Ban WHERE trangThai = 'Đang xử lý' ORDER BY id DESC")
    suspend fun getPendingBan(): List<Ban>

    @Query("SELECT COUNT(*) FROM Ban")
    suspend fun getBanCount(): Int

    @Query("SELECT * FROM Ban ORDER BY ngayThem DESC LIMIT 1")
    fun getLatestBanLive(): LiveData<Ban> // Đổi tên và trả về một bản ghi


    //  Đếm số đơn hàng "Đang xử lý"
    @Query("SELECT COUNT(*) FROM DonHang WHERE trangThai = 'Đang xử lý'")
    fun getPendingOrdersCount(): LiveData<Int>

    //  Đếm số đơn hàng "Đơn hàng đã được xác nhận"
    @Query("SELECT COUNT(*) FROM DonHang WHERE trangThai = 'Đơn hàng đã được xác nhận'")
    fun getConfirmedOrdersCount(): LiveData<Int>

    //  Tính tổng doanh thu từ các đơn "Khách đã thanh toán và nhận hàng"
    @Query("SELECT SUM(tongTien) FROM DonHang WHERE trangThai = 'Khách đã thanh toán và nhận hàng'")
    fun getTotalRevenue(): LiveData<Double?>
}