package com.example.ungdungdatdoanonlinenhvietkitchensg.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import com.example.ungdungdatdoanonlinenhvietkitchensg.dao.AppDao
import java.util.Calendar

@Database(
    entities = [
        NguoiDung::class, Admin::class, MonAn::class, GioHang::class,
        ChiTietGioHang::class, DonHang::class, ChiTietDonHang::class, // Thêm ChiTietDonHang
        LienHe::class, Ban::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "QuanLyDatDoAnOnline"
                ).build()

                INSTANCE = instance

                // Chèn dữ liệu mẫu chỉ khi cơ sở dữ liệu trống
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = instance.appDao()

                    // 1. Chèn dữ liệu vào bảng Admin
                    if (dao.getAdminCount() == 0) {
                        dao.insertAdmin(
                            Admin(
                                hoTen = "Admin",
                                email = "nhahangvietkitchensaigon@gmail.com", // Sửa email hợp lệ
                                matKhau = "admin123",
                                diaChi = "123 Nguyễn Công Trứ, Q1, TPHCM",
                                soDienThoai = "0999888888"
                            )
                        )
                    }

                    // 2. Chèn dữ liệu vào bảng NguoiDung
                    if (dao.getNguoiDungCount() == 0) {
                        dao.insertNguoiDung(
                            NguoiDung(
                                hoTen = "Nguyễn Đức Việt",
                                email = "ndv@gmail.com",
                                matKhau = "1",
                                diaChi = "314 Võ Văn Ngân, Thủ Đức, TPHCM",
                                soDienThoai = "0912345678"
                            )
                        )

                        dao.insertNguoiDung(
                            NguoiDung(
                                hoTen = "Vũ Xuân Hoàng Anh",
                                email = "vxha@gmail.com",
                                matKhau = "2",
                                diaChi = "265 Võ Văn Hát, Thủ Đức, TPHCM",
                                soDienThoai = "vxha@gmail.com"
                            )
                        )

                        dao.insertNguoiDung(
                            NguoiDung(
                                hoTen = "Nguyễn Thị Thu",
                                email = "ntt@gmail.com",
                                matKhau = "3",
                                diaChi = "325 Võ Nguyên Giáp, Quận 2, TPHCM",
                                soDienThoai = "0965436578"
                            )
                        )

                        dao.insertNguoiDung(
                            NguoiDung(
                                hoTen = "Nguyễn Thảo My",
                                email = "ntm@gmail.com",
                                matKhau = "4",
                                diaChi = "453 Võ Văn Hát, Thủ Đức, TPHCM",
                                soDienThoai = "0998798076"
                            )
                        )

                        dao.insertNguoiDung(
                            NguoiDung(
                                hoTen = "Trần Minh Quang",
                                email = "tmq@gmail.com",
                                matKhau = "5",
                                diaChi = "354 Võ Văn Hát, Thủ Đức, TPHCM",
                                soDienThoai = "0909765876"
                            )
                        )



                    }

                    /*// 2. Chèn dữ liệu vào bảng MonAn (đã thêm một bản ghi mẫu mới)
                    if (dao.getMonAnCount() == 0) {
                        dao.insertMonAn(
                            MonAn(
                                tenMon = "Bún Chả Hà Nội",
                                gia = 45000.0,
                                hinhAnh = "@drawable/banhmibo",
                                moTa = "Bún chả truyền thống với thịt nướng và nước mắm",
                                soLuong = 15,
                                nguyenLieu = "Thịt heo, bún, nước mắm, rau sống",
                                ngayThem = Date()
                            )
                        )
                        // Giữ nguyên bản ghi cũ nếu bạn muốn
                        dao.insertMonAn(
                            MonAn(
                                tenMon = "Phở Bò",
                                gia = 50000.0,
                                hinhAnh = "@drawable/banhmibo",
                                moTa = "Phở bò truyền thống thơm ngon",
                                soLuong = 10,
                                nguyenLieu = "Thịt bò, bánh phở, rau thơm",
                                ngayThem = Date()
                            )
                        )
                    }*/

                    // 3. Chèn 50 dữ liệu chi tiết vào bảng LienHe
                    if (dao.getLienHeCount() == 0) {
                        val lienHeList = listOf(
                            LienHe(chuDe = "Hỏi về món Phở Bò", noiDung = "Cho tôi hỏi Phở Bò có thể thêm nhiều rau thơm không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng Bún Chả", noiDung = "Tôi muốn đặt 3 phần Bún Chả Hà Nội giao đến 123 Lê Lợi, Q1 vào 12h trưa nay.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn giao hàng", noiDung = "Đơn hàng Phở Bò hôm qua giao trễ 30 phút, mong nhà hàng cải thiện.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Góp ý thực đơn", noiDung = "Nhà hàng có thể thêm món Cơm Tấm Sài Gòn không? Tôi rất thích món này.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi giờ mở cửa", noiDung = "Nhà hàng Việt Kitchen Sài Gòn mở cửa từ mấy giờ vậy?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen ngợi món ăn", noiDung = "Phở Bò của nhà hàng rất ngon, nước dùng đậm đà, sẽ quay lại!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Yêu cầu hóa đơn", noiDung = "Tôi đặt 5 phần Bún Chả hôm qua, có thể gửi hóa đơn qua email không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi nguyên liệu", noiDung = "Thịt trong Bún Chả Hà Nội là thịt heo gì? Có tươi không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt bàn", noiDung = "Tôi muốn đặt bàn cho 6 người vào tối thứ 7 tại Việt Kitchen Sài Gòn.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi chương trình khuyến mãi", noiDung = "Nhà hàng có ưu đãi gì cho đơn hàng trên 200k không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn chất lượng", noiDung = "Phở Bò hôm nay hơi nhạt, không giống lần trước tôi ăn.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về giao hàng", noiDung = "Nhà hàng có giao hàng đến quận 7 không? Phí bao nhiêu?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng số lượng lớn", noiDung = "Tôi muốn đặt 20 phần Phở Bò cho tiệc công ty, có giảm giá không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi thực đơn chay", noiDung = "Nhà hàng có món chay nào không? Tôi muốn thử.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen dịch vụ", noiDung = "Nhân viên giao hàng rất thân thiện, cảm ơn nhà hàng!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về nước mắm", noiDung = "Nước mắm trong Bún Chả có thể điều chỉnh độ mặn không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Yêu cầu giao sớm", noiDung = "Đơn hàng 2 phần Phở Bò có thể giao trước 11h sáng nay không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về đóng gói", noiDung = "Phở Bò giao hàng có để nước dùng riêng không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Góp ý dịch vụ", noiDung = "Nhà hàng nên thêm tùy chọn thanh toán qua ví điện tử.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về sự kiện", noiDung = "Nhà hàng có tổ chức buffet món Việt vào cuối tuần không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng đặc biệt", noiDung = "Tôi muốn Phở Bò không hành, giao đến 45 Pasteur, Q1.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi giá món ăn", noiDung = "Cho tôi xin giá món Bún Chả Hà Nội hiện tại.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn giao nhầm", noiDung = "Tôi đặt Phở Bò nhưng nhận được Bún Chả, mong kiểm tra lại.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về combo", noiDung = "Nhà hàng có combo Phở Bò và Bún Chả không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen nước dùng", noiDung = "Nước dùng Phở Bò rất thơm, đúng chuẩn Việt Nam!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi thời gian giao", noiDung = "Đặt 3 phần Bún Chả thì mất bao lâu để giao đến Q3?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Yêu cầu thêm rau", noiDung = "Phở Bò có thể thêm rau sống không? Tôi thích ăn nhiều rau.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về đặt tiệc", noiDung = "Nhà hàng có nhận đặt tiệc sinh nhật cho 15 người không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn đóng gói", noiDung = "Hộp Phở Bò bị đổ nước dùng khi giao, cần cải thiện.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về gia vị", noiDung = "Bún Chả có kèm ớt tươi không hay chỉ có nước mắm ớt?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng gấp", noiDung = "Tôi cần 4 phần Phở Bò giao gấp trong 30 phút tới 78 Nguyễn Huệ.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về số lượng", noiDung = "Bún Chả còn đủ 10 phần để giao ngay không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Góp ý món mới", noiDung = "Nhà hàng nên thử thêm món Gỏi Cuốn, rất hợp với thực đơn.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về đặt hàng online", noiDung = "Làm sao để theo dõi đơn hàng trên app của nhà hàng?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen nhân viên", noiDung = "Nhân viên tư vấn đặt hàng rất nhiệt tình, cảm ơn!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về topping", noiDung = "Phở Bò có thể thêm bò viên không? Giá bao nhiêu?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng định kỳ", noiDung = "Tôi muốn đặt Phở Bò giao mỗi sáng thứ 2, có được không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về vệ sinh", noiDung = "Nhà hàng đảm bảo vệ sinh an toàn thực phẩm như thế nào?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn giá cả", noiDung = "Giá Bún Chả tăng so với tháng trước, lý do là gì?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về nước uống", noiDung = "Đặt Phở Bò có kèm nước uống miễn phí không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Góp ý giao hàng", noiDung = "Nên thêm thông báo khi đơn hàng đang được giao.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về đặt bàn", noiDung = "Có thể đặt bàn gần cửa sổ cho 4 người không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen hương vị", noiDung = "Bún Chả nướng rất thơm, đúng chuẩn Hà Nội!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về trẻ em", noiDung = "Nhà hàng có ghế cho trẻ em không? Tôi dẫn bé 3 tuổi.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Đặt hàng đặc biệt", noiDung = "Phở Bò ít nước dùng, giao đến 56 Trần Hưng Đạo.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về sự kiện", noiDung = "Nhà hàng có chương trình gì đặc biệt vào Tết không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Phàn nàn số lượng", noiDung = "Phần Bún Chả hôm nay ít thịt hơn bình thường.", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về đặt hàng", noiDung = "Có thể đặt qua Zalo của nhà hàng không?", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Khen tổng thể", noiDung = "Dịch vụ và món ăn của Việt Kitchen Sài Gòn rất tuyệt!", hinhAnh = null, filetxt = null, fileExcel = null),
                            LienHe(chuDe = "Hỏi về thực đơn", noiDung = "Nhà hàng có cập nhật thực đơn mới chưa?", hinhAnh = null, filetxt = null, fileExcel = null)
                        )

                        lienHeList.forEach { lienHe ->
                            dao.insert(lienHe)
                        }
                    }



                    /* // 3. Chèn dữ liệu vào bảng MonAn
                   if (dao.getMonAnCount() == 0) {
                       dao.insertMonAn(
                           MonAn(
                               tenMon = "Phở Bò",
                               gia = 50000.0,
                               hinhAnh = "@drawable/pho_bo",
                               moTa = "Phở bò truyền thống thơm ngon",
                               soLuong = 10,
                               nguyenLieu = "Thịt bò, bánh phở, rau thơm"
                           )
                       )
                   }

                   // 4. Chèn dữ liệu vào bảng GioHang
                   if (dao.getGioHangCount() == 0) {
                       dao.insertGioHang(
                           GioHang(
                               idNguoiDung = 1
                           )
                       )
                   }

                   // 5. Chèn dữ liệu vào bảng ChiTietGioHang
                   if (dao.getChiTietGioHangCount() == 0) {
                       dao.insertChiTietGioHang(
                           ChiTietGioHang(
                               idGioHang = 1,
                               idMonAn = 1,
                               soLuong = 2
                           )
                       )
                   }

                   // 6. Chèn dữ liệu vào bảng DonHang
                   if (dao.getDonHangCount() == 0) {
                       dao.insertDonHang(
                           DonHang(
                               idNguoiDung = 1,
                               hoTen = "Nguyễn Đức Việt",
                               diaChi = "314 Võ Văn Ngân, Thủ Đức, TPHCM",
                               soDienThoai = "0912345678",
                               phuongThucThanhToan = "Thanh toán khi nhận hàng",
                               tenChuThe = null,
                               soThe = null,
                               ngayHetHan = null,
                               cvvNo = null,
                               tongTien = 100000.0,
                               trangThai = "Đang xử lý"
                           )
                       )
                   }

                   // 7. Chèn dữ liệu vào bảng ChiTietDonHang
                   if (dao.getChiTietDonHangCount() == 0) {
                       dao.insertChiTietDonHang(
                           ChiTietDonHang(
                               idDonHang = 1,
                               idMonAn = 1,
                               soLuong = 2
                           )
                       )
                   }

                   */
                    // Chèn 50 dữ liệu chi tiết vào bảng Ban
                    if (dao.getBanCount() == 0) {
                        val banList = listOf(
                            Ban(tenKhachDat = "Nguyễn Văn Cảnh", soDienThoai = "0912345678", soLuongKhach = 2, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 15), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Trần Thị Mai", soDienThoai = "0923456789", soLuongKhach = 3, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 3, 16), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Lê Đức Hùng", soDienThoai = "0934567890", soLuongKhach = 1, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 3, 17), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Phạm Minh Lan", soDienThoai = "0945678901", soLuongKhach = 4, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 18), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Hoàng Thanh Tâm", soDienThoai = "0956789012", soLuongKhach = 5, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 3, 19), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Đỗ Quốc Phong", soDienThoai = "0967890123", soLuongKhach = 2, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 3, 20), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Vũ Hồng Bình", soDienThoai = "0978901234", soLuongKhach = 3, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 21), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Nguyễn Thị Hạnh", soDienThoai = "0989012345", soLuongKhach = 1, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 3, 22), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Trần Văn Dũng", soDienThoai = "0990123456", soLuongKhach = 4, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 3, 23), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Lê Minh Thắng", soDienThoai = "0911234567", soLuongKhach = 5, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 24), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Phạm Thị Ngọc", soDienThoai = "0922345678", soLuongKhach = 2, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 3, 25), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Hoàng Văn Long", soDienThoai = "0933456789", soLuongKhach = 3, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 3, 26), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Đỗ Thị Thu", soDienThoai = "0944567890", soLuongKhach = 1, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 27), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Vũ Quốc Anh", soDienThoai = "0955678901", soLuongKhach = 4, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 3, 28), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Nguyễn Thanh Hà", soDienThoai = "0966789012", soLuongKhach = 5, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 3, 29), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Trần Đức Tuấn", soDienThoai = "0977890123", soLuongKhach = 2, trangThai = "Đang xử lý", ngayDat = createDate(2025, 3, 30), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Lê Thị Hoa", soDienThoai = "0988901234", soLuongKhach = 3, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 1), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Phạm Văn Hùng", soDienThoai = "0999012345", soLuongKhach = 1, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 2), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Hoàng Thị Linh", soDienThoai = "0910123456", soLuongKhach = 4, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 3), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Đỗ Văn Nam", soDienThoai = "0921234567", soLuongKhach = 5, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 4), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Vũ Thị Yến", soDienThoai = "0932345678", soLuongKhach = 2, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 5), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Nguyễn Minh Khôi", soDienThoai = "0943456789", soLuongKhach = 3, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 6), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Trần Thị Ánh", soDienThoai = "0954567890", soLuongKhach = 1, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 7), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Lê Văn Tùng", soDienThoai = "0965678901", soLuongKhach = 4, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 8), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Phạm Thị Bích", soDienThoai = "0976789012", soLuongKhach = 5, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 9), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Hoàng Văn Sơn", soDienThoai = "0987890123", soLuongKhach = 2, trangThai = "Yêu cầu đặt nhận", ngayDat = createDate(2025, 4, 10), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Đỗ Thị Hương", soDienThoai = "0998901234", soLuongKhach = 3, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 11), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Vũ Minh Đức", soDienThoai = "0919012345", soLuongKhach = 1, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 12), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Nguyễn Thị Phương", soDienThoai = "0920123456", soLuongKhach = 4, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 13), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Trần Văn Khoa", soDienThoai = "0931234567", soLuongKhach = 5, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 14), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Lê Thị Kim", soDienThoai = "0942345678", soLuongKhach = 2, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 15), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Phạm Văn Phúc", soDienThoai = "0953456789", soLuongKhach = 3, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 16), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Hoàng Thị Dung", soDienThoai = "0964567890", soLuongKhach = 1, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 17), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Đỗ Văn Tài", soDienThoai = "0975678901", soLuongKhach = 4, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 18), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Vũ Thị Oanh", soDienThoai = "0986789012", soLuongKhach = 5, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 19), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Nguyễn Văn Hải", soDienThoai = "0997890123", soLuongKhach = 2, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 20), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Trần Thị Lệ", soDienThoai = "0918901234", soLuongKhach = 3, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 21), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Lê Văn Hoàng", soDienThoai = "0929012345", soLuongKhach = 1, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 22), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Phạm Thị Quyên", soDienThoai = "0930123456", soLuongKhach = 4, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 23), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Hoàng Văn Thắng", soDienThoai = "0941234567", soLuongKhach = 5, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 24), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Đỗ Thị Ngọc", soDienThoai = "0952345678", soLuongKhach = 2, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 25), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Vũ Văn Kiên", soDienThoai = "0963456789", soLuongKhach = 3, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 26), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Nguyễn Thị Thảo", soDienThoai = "0974567890", soLuongKhach = 1, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 27), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Trần Văn Toàn", soDienThoai = "0985678901", soLuongKhach = 4, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 28), ngayThem = createDate(2025, 3, 4)),
                            Ban(tenKhachDat = "Lê Thị Hiền", soDienThoai = "0996789012", soLuongKhach = 5, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 29), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Phạm Văn Lợi", soDienThoai = "0917890123", soLuongKhach = 2, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 30), ngayThem = createDate(2025, 3, 1)),
                            Ban(tenKhachDat = "Hoàng Thị Xuân", soDienThoai = "0928901234", soLuongKhach = 3, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 1), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Đỗ Văn Quang", soDienThoai = "0939012345", soLuongKhach = 1, trangThai = "Đã bị huỷ", ngayDat = createDate(2025, 4, 2), ngayThem = createDate(2025, 3, 2)),
                            Ban(tenKhachDat = "Vũ Thị Nhung", soDienThoai = "0940123456", soLuongKhach = 4, trangThai = "Đang xử lý", ngayDat = createDate(2025, 4, 3), ngayThem = createDate(2025, 3, 3)),
                            Ban(tenKhachDat = "Nguyễn Văn Đông", soDienThoai = "0951234567", soLuongKhach = 5, trangThai = "Yêu cầu đặt bàn đã được xác nhận", ngayDat = createDate(2025, 4, 4), ngayThem = createDate(2025, 3, 3))
                        )

                        banList.forEach { ban ->
                            dao.insertBan(ban)
                        }
                    }
                }

                instance
            }
        }

        // Hàm hỗ trợ tạo Date
        private fun createDate(year: Int, month: Int, day: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            return calendar.time
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}