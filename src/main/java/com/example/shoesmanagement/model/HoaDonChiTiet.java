package com.example.shoesmanagement.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name= "Hoa_Don_Chi_Tiet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_HDCT")
    private UUID idHDCT;

    @ManyToOne
    @JoinColumn(name = "id_CTG")
    private ChiTietGiay chiTietGiay;

    @ManyToOne
    @JoinColumn(name = "id_HD")
    private HoaDon hoaDon;

    @Column(name = "don_Gia")
    private Double donGia;

    @Column(name = "so_Luong")
    private int soLuong;

    @Column(name = "trang_Thai")
    private Integer trangThai;

    @Column(name = "tg_Them")
    private Date tgThem;
    @Transient
    private Double tongTien;

    // Phương thức getter để tính tổng tiền
    public Double getTongTien() {
        if (donGia != null && soLuong > 0) {
            return donGia * soLuong;
        }
        return 0.0;
    }


}
