package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RequestMapping("/manage/bill/")
@Controller
public class HoaDonOnlineController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private GiaoHangService giaoHangService;


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;
    @Autowired
    private GiayViewModelService giayViewModelService;
    @Autowired
    private LSThanhToanService lsThanhToanService;
    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private ChucVuService chucVuService;
    @Autowired
    private HttpSession httpSession;

    @GetMapping("online")
    private String manageBillOnline(Model model) {
        model.addAttribute("reLoadPage", true);
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        showData(model);
        showTab1(model);


        return "manage/manage-bill-online";
    }

    @GetMapping("online/delivery/{idHD}")
    private String editBillOnline(Model model, @PathVariable UUID idHD) {

        HoaDon billDelivery = hoaDonService.getOne(idHD);
        List<NhanVien> listNhanVienGiao = new ArrayList<>();

        ChucVu nvgh = chucVuService.findByMaCV("GH01");

        List<NhanVien> listNVGH = nhanVienService.findByChucVu(nvgh);

        if (listNVGH != null) {
            for (NhanVien x : listNVGH) {
                listNhanVienGiao.add(x);
            }
        }

        model.addAttribute("listNhanVienGiao", listNhanVienGiao);
        model.addAttribute("billDelivery", billDelivery);
        model.addAttribute("showEditBillDelivery", true);

        showTab3(model);

        showData(model);


        return "manage/manage-bill-online";
    }

    @PostMapping("online/delivery/confirm/{idHD}")
    private String confirmNVGH(Model model, @PathVariable UUID idHD) {

        UUID idNV = UUID.fromString(request.getParameter("idNhanVien"));

        Date date = new Date();
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(idNV);
        HoaDon hoaDon = hoaDonService.getOne(idHD);

        hoaDon.setNhanVien(nhanVien);
        hoaDon.setTrangThai(2);

        hoaDonService.add(hoaDon);

        GiaoHang giaoHang = hoaDon.getGiaoHang();
        giaoHang.setNoiDung("Xác nhận nhân viên giao hàng");
        giaoHang.setTgShip(date);
        giaoHangService.saveGiaoHang(giaoHang);

        hoaDon.setGiaoHang(giaoHang);
        hoaDonService.add(hoaDon);


        showData(model);

        model.addAttribute("showMessThanhCong", true);
        model.addAttribute("reLoadPage", true);
        showData(model);
        showTab3(model);
        model.addAttribute("messageXacNhan", "Xác nhận nhân viên giao thành công.");

        return "manage/manage-bill-online";
    }

    @GetMapping("online/delete/{idHD}")
    public String huyHoaDonOnline(@PathVariable UUID idHD, Model model) {
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        String lyDoHuy = request.getParameter("lyDoHuy");
        Date date = new Date();
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        hoaDon.setLyDoHuy(lyDoHuy);
        hoaDon.setTgHuy(date);
        hoaDon.setTrangThai(5);

        hoaDonService.save(hoaDon);

        showData(model);
        showTab3(model);
        model.addAttribute("message", "Hóa đơn đã được hủy thành công.");
        return "manage/manage-bill-online";
    }

    private void showTab1(Model model) {
        model.addAttribute("activeAll", "nav-link active");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link");

        model.addAttribute("tabpane1", "tab-pane show active");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane");
        model.addAttribute("tabpane4", "tab-pane");

    }

    private void showTab3(Model model) {

        model.addAttribute("activeAll", "nav-link");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link active");


        model.addAttribute("tabpane1", "tab-pane");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane show active");
        model.addAttribute("tabpane4", "tab-pane");
    }

    private void showData(Model model) {

        List<HoaDon> listAllHoaDonOnline = hoaDonService.listHoaDonOnline();


        List<HoaDon> listAllHoaDonDangGiao = new ArrayList<>();
        List<HoaDon> listHoaDonOnlineBaking = new ArrayList<>();


        int soLuongHoaDonOnline = 0;
        int soLuongHoaDonHuy = 0;
        int soLuongHoaDonDaThanhToan = 0;
        int soLuongHoaDonChuaThanhToanNhanHang = 0;
        int soLuongHoaDonDangGiao = 0;
        int soLuongHoaDonBanking = 0;
        int soLuongHoaDonDaNhan = 0;
        int soLuongHoaDonChoXacNhan = 0;
        int soLuongHoaDonChoLayHang = 0;


        int soLuongHoaDonThanhToanKhiNhanHang = 0;

        double tongTienHoaDon = 0.0;
        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
                    System.out.println("abc");
                } else {
                    tongTienHoaDon += x.getTongTien();
                }
            }
        }

        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
                    System.out.println("abc");
                } else {
                    if (x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonBanking++;
                        listHoaDonOnlineBaking.add(x);
                    }
                    if (x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonThanhToanKhiNhanHang++;
                    }

                    if (x.getTrangThai() == 1 && x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 4 && x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 1) {
                        soLuongHoaDonChoXacNhan++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 2) {
                        soLuongHoaDonChoLayHang++;
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 5) {
                        soLuongHoaDonHuy++;
                    }
                    if (x.getTrangThai() == 4) {
                        soLuongHoaDonDaNhan++;
                    }
                }
            }
        }

        int soLuongHoaDonChuaThanhToan = soLuongHoaDonChuaThanhToanNhanHang + soLuongHoaDonBanking;


        int soLuongHdHoanChoXacNhan = 0;
        int soLuongHdHoanKhachHuy = 0;


        model.addAttribute("sumBillOnline", soLuongHoaDonOnline);
        model.addAttribute("totalAmount", tongTienHoaDon);
        model.addAttribute("sumQuantityBaking", soLuongHoaDonBanking);
        model.addAttribute("sumQuantityDelivery", soLuongHoaDonThanhToanKhiNhanHang);
        model.addAttribute("hoaDonChuaThanhToan", soLuongHoaDonChuaThanhToan);
        model.addAttribute("hoaDonDaThanhToan", soLuongHoaDonDaThanhToan);
        model.addAttribute("hoaDonDangGiao", soLuongHoaDonDangGiao);
        model.addAttribute("hoaDonChoXacNhan", soLuongHoaDonChoXacNhan);
        model.addAttribute("hoaDonChoLayHang", soLuongHoaDonChoLayHang);
        model.addAttribute("hoaDonHuy", soLuongHoaDonHuy);
        model.addAttribute("hoaDonDaNhan", soLuongHoaDonDaNhan);

        model.addAttribute("soLuongHdHoanChoXacNhan", soLuongHdHoanChoXacNhan);
        model.addAttribute("soLuongHdHoanKhachHuy", soLuongHdHoanKhachHuy);

        model.addAttribute("listHoaDonOnline", listAllHoaDonOnline);
        model.addAttribute("listHoaDonOnlineGiaoHang", listAllHoaDonDangGiao);
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false)String keyword, Model model,
                         RedirectAttributes redirectAttributes) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        if (keyword.length() >= 3 && keyword.substring(0, 3).equals("CTG")) {
            ChiTietGiay chiTietGiay = giayChiTietService.findByMa(keyword);
            if (chiTietGiay == null) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản mã phẩm ");
                return "redirect:/manage/bill/online" ;
            }
            model.addAttribute("idHoaDon", idHoaDon);
        } else {
            List<GiayViewModel> list = giayViewModelService.getAll(keyword);
            if (list.isEmpty()) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản phẩm");
                return "redirect:/manage/bill/online" ;
            }
            model.addAttribute("listSanPham", list);
        }

        return "manage/manage-bill-online";
    }

}
