package com.example.test2.Sach;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.Adapter.SachAdapter;
import com.example.test2.DAO.LoaiSachDAO;
import com.example.test2.DAO.SachDAO;
import com.example.test2.R;
import com.example.test2.model.LoaiSach;
import com.example.test2.model.Sach;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;
import com.example.test2.Adapter.LoaiSachSpinnerAdapter;
import androidx.appcompat.widget.ListPopupWindow;

public class SachFragment extends Fragment {
    ListView lvSach;
    ArrayList<Sach> list;
    SachDAO dao;
    SachAdapter adapter;
    FloatingActionButton fab;
    Dialog dialog;
    EditText edTenSach, edGiaThue, edSearch;
    Spinner spLoaiSach;
    Button btnSave, btnCancel;
    TextView tvLoaiSachLabel, tvMaSach, tvEmpty;
    LinearLayout layout_spLoaiSach;
    LoaiSachDAO loaiSachDAO;
    int maLoaiSach;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sach, container, false);
        lvSach = v.findViewById(R.id.lvSach);
        edSearch = v.findViewById(R.id.edSearch);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        fab = v.findViewById(R.id.fab);
        dao = new SachDAO(getActivity());

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
                openDialog(getActivity(), 0);
            }
        });

        capNhatLv();
        return v;
    }

    void capNhatLv() {
        list = (ArrayList<Sach>) dao.getAll();
        adapter = new SachAdapter(getActivity(), this, list);
        lvSach.setAdapter(adapter);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void xoa(final String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có muốn xóa không?");
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

    public void openDialog(final Context context, final int type, final Sach... sachObj) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_sach);
        edTenSach = dialog.findViewById(R.id.edTenSach);
        edGiaThue = dialog.findViewById(R.id.edGiaThue);
        spLoaiSach = dialog.findViewById(R.id.spLoaiSach);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);
        tvLoaiSachLabel = dialog.findViewById(R.id.tvLoaiSachLabel);
        tvMaSach = dialog.findViewById(R.id.tvMaSach);
        layout_spLoaiSach = dialog.findViewById(R.id.layout_spLoaiSach);

        loaiSachDAO = new LoaiSachDAO(context);
        ArrayList<LoaiSach> listLoai = (ArrayList<LoaiSach>) loaiSachDAO.getAll();
        LoaiSachSpinnerAdapter loaiSachSpinnerAdapter = new LoaiSachSpinnerAdapter(context, listLoai);
        
        final ListPopupWindow popupLoaiSach = new ListPopupWindow(context);
        popupLoaiSach.setAdapter(loaiSachSpinnerAdapter);
        popupLoaiSach.setAnchorView(layout_spLoaiSach);
        popupLoaiSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoaiSach ls = listLoai.get(position);
                maLoaiSach = ls.getMaLoai();
                tvLoaiSachLabel.setText("Loại sách: " + ls.getTenLoai());
                popupLoaiSach.dismiss();
            }
        });

        if (type == 0) {
            tvMaSach.setVisibility(View.GONE);
        } else if (type != 0 && sachObj.length > 0) {
            Sach item = sachObj[0];
            tvMaSach.setVisibility(View.VISIBLE);
            tvMaSach.setText("Mã sách: " + item.getMaSach());
            edTenSach.setText(item.getTenSach());
            edGiaThue.setText(String.valueOf(item.getGiaThue()));
            maLoaiSach = item.getMaLoai();
            
            LoaiSach currentLoai = loaiSachDAO.getID(String.valueOf(maLoaiSach));
            if (currentLoai != null) {
                tvLoaiSachLabel.setText("Loại sách: " + currentLoai.getTenLoai());
            }
            btnSave.setText("Cập nhật");
        }

        layout_spLoaiSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupLoaiSach.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSach = edTenSach.getText().toString();
                String giaThueStr = edGiaThue.getText().toString();
                
                if (tenSach.isEmpty() || giaThueStr.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (maLoaiSach == 0) {
                    Toast.makeText(context, "Vui lòng chọn loại sách", Toast.LENGTH_SHORT).show();
                    return;
                }

                Sach item = (type == 0) ? new Sach() : sachObj[0];
                item.setTenSach(tenSach);
                item.setGiaThue(Integer.parseInt(giaThueStr));
                item.setMaLoai(maLoaiSach);

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
