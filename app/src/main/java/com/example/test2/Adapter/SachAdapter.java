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

import com.example.test2.DAO.LoaiSachDAO;
import com.example.test2.R;
import com.example.test2.Sach.SachFragment;
import com.example.test2.model.LoaiSach;
import com.example.test2.model.Sach;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SachAdapter extends ArrayAdapter<Sach> {
    private final Context context;
    private final SachFragment fragment;
    private final ArrayList<Sach> list;
    TextView tvMaSach, tvTenSach, tvGiaThue, tvLoai, tvSoLuong;
    ImageView imgDel;

    public SachAdapter(@NonNull Context context, SachFragment fragment, ArrayList<Sach> list) {
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
            v = inflater.inflate(R.layout.item_sach, null);
        }
        final Sach item = list.get(position);
        if (item != null) {
            tvMaSach = v.findViewById(R.id.tvMaSach);
            tvMaSach.setText("Mã sách: " + item.getMaSach());

            tvTenSach = v.findViewById(R.id.tvTenSach);
            tvTenSach.setText("Tên sách: " + item.getTenSach());

            tvGiaThue = v.findViewById(R.id.tvGiaThue);
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedGia = formatter.format(item.getGiaThue()).replace(",", ".");
            tvGiaThue.setText("Giá thuê: " + formattedGia);

            tvSoLuong = v.findViewById(R.id.tvSoLuong);
            MaterialCardView cardView = (MaterialCardView) v;
            if (item.getSoLuong() == 0) {
                tvSoLuong.setText("Hết sách");
                tvSoLuong.setTextColor(Color.RED);
                cardView.setCardBackgroundColor(Color.parseColor("#FFF0F0"));
                cardView.setStrokeWidth(3);
                cardView.setStrokeColor(Color.RED);
            } else if (item.getSoLuong() <= 5) {
                tvSoLuong.setText("Sắp hết");
                tvSoLuong.setTextColor(Color.parseColor("#FFA500")); // Orange/Yellow
                cardView.setCardBackgroundColor(Color.parseColor("#FFF9F0"));
                cardView.setStrokeWidth(3);
                cardView.setStrokeColor(Color.parseColor("#FFA500"));
            } else {
                tvSoLuong.setText("Số lượng: " + item.getSoLuong());
                tvSoLuong.setTextColor(Color.parseColor("#555555"));
                cardView.setCardBackgroundColor(Color.WHITE);
                cardView.setStrokeWidth(0);
            }

            LoaiSachDAO loaiSachDAO = new LoaiSachDAO(context);
            LoaiSach loaiSach = loaiSachDAO.getID(String.valueOf(item.getMaLoai()));
            tvLoai = v.findViewById(R.id.tvLoai);
            if (loaiSach != null) {
                tvLoai.setText("Loại sách: " + loaiSach.getTenLoai());
            }

            imgDel = v.findViewById(R.id.imgDel);
            imgDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.xoa(String.valueOf(item.getMaSach()));
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
