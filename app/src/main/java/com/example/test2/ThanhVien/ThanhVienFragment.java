package com.example.test2.ThanhVien;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.Adapter.ThanhVienAdapter;
import com.example.test2.DAO.ThanhVienDAO;
import com.example.test2.R;
import com.example.test2.model.ThanhVien;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ThanhVienFragment extends Fragment {
    ListView lvThanhVien;
    ArrayList<ThanhVien> list;
    ThanhVienDAO dao;
    ThanhVienAdapter adapter;
    FloatingActionButton fab;
    Dialog dialog;
    EditText edHoTen, edNamSinh, edSDT, edDiaChi, edSearch;
    TextView tvMaTV, tvEmpty;
    MaterialButton btnSave, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_thanh_vien, container, false);
        lvThanhVien = v.findViewById(R.id.lvThanhVien);
        edSearch = v.findViewById(R.id.edSearch);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        fab = v.findViewById(R.id.fab);
        dao = new ThanhVienDAO(getActivity());

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list.clear();
                list.addAll(dao.search(s.toString()));
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(getActivity(), 0); // 0 là thêm mới
            }
        });

        capNhatLv();
        return v;
    }

    void capNhatLv() {
        list = (ArrayList<ThanhVien>) dao.getAll();
        adapter = new ThanhVienAdapter(getActivity(), this, list);
        lvThanhVien.setAdapter(adapter);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void xoa(final String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có muốn xóa thành viên này không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.delete(Id);
                capNhatLv();
            }
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }

    public void openDialog(final Context context, final int type, final ThanhVien... obj) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_thanh_vien);
        tvMaTV = dialog.findViewById(R.id.tvMaTV);
        edHoTen = dialog.findViewById(R.id.edHoTen);
        edNamSinh = dialog.findViewById(R.id.edNamSinh);
        edSDT = dialog.findViewById(R.id.edSDT);
        edDiaChi = dialog.findViewById(R.id.edDiaChi);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);

        if (type == 0) {
            tvMaTV.setVisibility(View.GONE);
        } else {
            tvMaTV.setVisibility(View.VISIBLE);
            tvMaTV.setText("Mã thành viên: " + obj[0].getMaTV());
            edHoTen.setText(obj[0].getHoTen());
            edNamSinh.setText(obj[0].getNamSinh());
            edSDT.setText(obj[0].getSdt());
            edDiaChi.setText(obj[0].getDiaChi());
            btnSave.setText("Cập nhật");
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = edHoTen.getText().toString();
                String namSinh = edNamSinh.getText().toString();
                String sdt = edSDT.getText().toString();
                String diaChi = edDiaChi.getText().toString();

                if (hoTen.isEmpty() || namSinh.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                ThanhVien item = (type == 0) ? new ThanhVien() : obj[0];
                item.setHoTen(hoTen);
                item.setNamSinh(namSinh);
                item.setSdt(sdt);
                item.setDiaChi(diaChi);

                if (type == 0) {
                    if (dao.insert(item) > 0) {
                        Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (dao.update(item) > 0) {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                capNhatLv();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
