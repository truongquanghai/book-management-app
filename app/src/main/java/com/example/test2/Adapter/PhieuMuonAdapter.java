package com.example.test2.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test2.DAO.SachDAO;
import com.example.test2.DAO.ThanhVienDAO;
import com.example.test2.R;
import com.example.test2.model.PhieuMuon;
import com.example.test2.model.Sach;
import com.example.test2.model.ThanhVien;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.test2.PhieuMuon.PhieuMuonFragment;

public class PhieuMuonAdapter extends ArrayAdapter<PhieuMuon> {
    private final Context context;
    private final ArrayList<PhieuMuon> list;
    private final PhieuMuonFragment fragment;
    TextView tvMaPM, tvTenTV, tvTenSach, tvTienThue, tvNgay, tvHanTra, tvNgayTra;
    ImageView imgDel;
    com.google.android.material.card.MaterialCardView cardPhieuMuon;
    ThanhVienDAO thanhVienDAO;
    SachDAO sachDAO;

    public PhieuMuonAdapter(@NonNull Context context, PhieuMuonFragment fragment, ArrayList<PhieuMuon> list) {
        super(context, 0, list);
        this.context = context;
        this.fragment = fragment;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_phieu_muon, null);
        }
        final PhieuMuon item = list.get(position);
        if (item != null) {
            tvMaPM = v.findViewById(R.id.tvMaPM);
            tvMaPM.setText("Mã phiếu: " + item.getMaPM());

            thanhVienDAO = new ThanhVienDAO(context);
            ThanhVien thanhVien = thanhVienDAO.getID(String.valueOf(item.getMaTV()));
            tvTenTV = v.findViewById(R.id.tvTenTV);
            if (thanhVien != null) {
                tvTenTV.setText("Thành viên: " + thanhVien.getHoTen());
            }

            sachDAO = new SachDAO(context);
            Sach sach = sachDAO.getID(String.valueOf(item.getMaSach()));
            tvTenSach = v.findViewById(R.id.tvTenSach);
            if (sach != null) {
                tvTenSach.setText("Sách: " + sach.getTenSach());
            }

            tvTienThue = v.findViewById(R.id.tvTienThue);
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedTienThue = formatter.format(item.getTienThue()).replace(",", ".");
            
            sachDAO = new SachDAO(context);
            Sach sachGoc = sachDAO.getID(String.valueOf(item.getMaSach()));
            if (sachGoc != null) {
                int giaGoc = sachGoc.getGiaThue();
                if (item.getTienThue() > giaGoc) {
                    String formattedPhat = formatter.format(item.getTienThue() - giaGoc).replace(",", ".");
                    tvTienThue.setText("Tiền thuê: " + formattedTienThue + " (Phạt: " + formattedPhat + ")");
                    tvTienThue.setTextColor(Color.RED);
                } else {
                    tvTienThue.setText("Tiền thuê: " + formattedTienThue);
                    tvTienThue.setTextColor(Color.BLACK);
                }
            } else {
                tvTienThue.setText("Tiền thuê: " + formattedTienThue);
            }

            tvNgay = v.findViewById(R.id.tvNgay);
            tvNgay.setText("Ngày mượn: " + item.getNgay());

            tvHanTra = v.findViewById(R.id.tvHanTra);
            tvHanTra.setText("Hạn trả: " + item.getHanTra());

            cardPhieuMuon = v.findViewById(R.id.cardPhieuMuon);
            tvNgayTra = v.findViewById(R.id.tvNgayTra);
            if (item.getNgayTra() != null && !item.getNgayTra().isEmpty()) {
                tvNgayTra.setText("Ngày trả: " + item.getNgayTra());
                tvNgayTra.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh cho đã trả
                cardPhieuMuon.setCardBackgroundColor(Color.WHITE); // Mặc định là trắng
                
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date dHanTra = sdf.parse(item.getHanTra());
                    java.util.Date dNgayTra = sdf.parse(item.getNgayTra());
                    if (dNgayTra.after(dHanTra)) {
                        tvNgayTra.setText("Ngày trả: " + item.getNgayTra() + " (Muộn)");
                        tvNgayTra.setTextColor(Color.RED);
                        cardPhieuMuon.setCardBackgroundColor(Color.parseColor("#FFF9C4")); // Màu vàng nhạt cho trả muộn
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tvNgayTra.setText("Ngày trả: Chưa trả");
                tvNgayTra.setTextColor(Color.parseColor("#FF5252"));
                cardPhieuMuon.setCardBackgroundColor(Color.WHITE);
                
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date dHanTra = sdf.parse(item.getHanTra());
                    java.util.Date dHienTai = new java.util.Date();
                    if (dHienTai.after(dHanTra)) {
                        tvHanTra.setTextColor(Color.RED);
                        tvHanTra.setText("Hạn trả: " + item.getHanTra() + " (QUÁ HẠN)");
                        cardPhieuMuon.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Màu đỏ nhạt cho quá hạn
                    } else {
                        tvHanTra.setTextColor(Color.parseColor("#757575"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            imgDel = v.findViewById(R.id.imgDel);
            imgDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.xoa(String.valueOf(item.getMaPM()));
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.openDialog(context, 1, item);
                }
            });
        }
        return v;
    }
}
