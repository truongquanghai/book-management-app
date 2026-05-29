package com.example.test2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test2.R;
import com.example.test2.ThanhVien.ThanhVienFragment;
import com.example.test2.model.ThanhVien;

import java.util.ArrayList;

public class ThanhVienAdapter extends ArrayAdapter<ThanhVien> {
    private final Context context;
    private final ThanhVienFragment fragment;
    private final ArrayList<ThanhVien> list;
    TextView tvMaTV, tvHoTen, tvNamSinh, tvSDT, tvDiaChi;
    ImageView imgDel;

    public ThanhVienAdapter(@NonNull Context context, ThanhVienFragment fragment, ArrayList<ThanhVien> list) {
        super(context, 0, list);
        this.context = context;
        this.fragment = fragment;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_thanh_vien, parent, false);
        }
        
        final ThanhVien item = list.get(position);
        if (item != null) {
            TextView tvMaTV = convertView.findViewById(R.id.tvMaTV);
            TextView tvHoTen = convertView.findViewById(R.id.tvHoTen);
            TextView tvNamSinh = convertView.findViewById(R.id.tvNamSinh);
            TextView tvSDT = convertView.findViewById(R.id.tvSDT);
            ImageView imgDel = convertView.findViewById(R.id.imgDel);

            tvMaTV.setText("Mã: " + item.getMaTV());
            tvHoTen.setText(item.getHoTen());
            tvNamSinh.setText("NS: " + item.getNamSinh());
            tvSDT.setText("SĐT: " + item.getSdt());

            convertView.setOnClickListener(v -> fragment.openDialog(context, 1, item));

            imgDel.setOnClickListener(v -> fragment.xoa(String.valueOf(item.getMaTV())));
        }
        return convertView;
    }
}
