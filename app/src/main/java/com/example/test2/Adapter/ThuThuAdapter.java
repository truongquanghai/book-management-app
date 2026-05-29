package com.example.test2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test2.AddUser.AddUserFragment;
import com.example.test2.DAO.ThuThuDAO;
import com.example.test2.R;
import com.example.test2.model.ThuThu;

import java.util.ArrayList;

public class ThuThuAdapter extends ArrayAdapter<ThuThu> {
    private final Context context;
    private final ArrayList<ThuThu> list;
    private final AddUserFragment fragment;
    private final ThuThuDAO dao;

    public ThuThuAdapter(@NonNull Context context, AddUserFragment fragment, ArrayList<ThuThu> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        this.dao = new ThuThuDAO(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_thu_thu, parent, false);
        }

        ThuThu item = list.get(position);
        if (item != null) {
            TextView tvHoTen = convertView.findViewById(R.id.tvHoTen);
            TextView tvMaTT = convertView.findViewById(R.id.tvMaTT);
            TextView tvEmail = convertView.findViewById(R.id.tvEmail);
            ImageView imgDel = convertView.findViewById(R.id.imgDel);

            tvHoTen.setText(item.getHoTen());
            tvMaTT.setText("Mã: " + item.getMaTT());
            tvEmail.setText(item.getEmail());

            // Không cho phép xóa tài khoản admin
            if (item.getMaTT().equalsIgnoreCase("admin")) {
                imgDel.setVisibility(View.INVISIBLE);
            } else {
                imgDel.setVisibility(View.VISIBLE);
                imgDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(item);
                    }
                });
            }
        }
        return convertView;
    }

    private void showDeleteDialog(ThuThu thuThu) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhân viên " + thuThu.getHoTen() + " không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dao.delete(thuThu.getMaTT()) > 0) {
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    fragment.updateList();
                } else {
                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
