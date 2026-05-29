package com.example.test2.AddUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.Adapter.ThuThuAdapter;
import com.example.test2.DAO.ThuThuDAO;
import com.example.test2.R;
import com.example.test2.model.ThuThu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AddUserFragment extends Fragment {
    ListView lvThuThu;
    TextView tvEmpty;
    FloatingActionButton fab;
    ArrayList<ThuThu> list;
    ThuThuAdapter adapter;
    ThuThuDAO dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_user, container, false);
        lvThuThu = v.findViewById(R.id.lvThuThu);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        fab = v.findViewById(R.id.fab);
        dao = new ThuThuDAO(getActivity());

        updateList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle("Nhân viên của bạn");
        }
    }

    public void updateList() {
        list = (ArrayList<ThuThu>) dao.getAll();
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvThuThu.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvThuThu.setVisibility(View.VISIBLE);
        }
        adapter = new ThuThuAdapter(getActivity(), this, list);
        lvThuThu.setAdapter(adapter);
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_thu_thu, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        TextInputEditText edUser = view.findViewById(R.id.edUser);
        TextInputEditText edHoTen = view.findViewById(R.id.edHoTen);
        TextInputEditText edPass = view.findViewById(R.id.edPass);
        TextInputEditText edRePass = view.findViewById(R.id.edRePass);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edUser.getText().toString();
                String hoTen = edHoTen.getText().toString();
                String pass = edPass.getText().toString();
                String rePass = edRePass.getText().toString();

                if (user.isEmpty() || hoTen.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(rePass)) {
                    Toast.makeText(getContext(), "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                ThuThu thuThu = new ThuThu();
                thuThu.setMaTT(user);
                thuThu.setHoTen(hoTen);
                thuThu.setMatKhau(pass);
                thuThu.setEmail(user + "@gmail.com");
                thuThu.setAvatar("");

                if (dao.insert(thuThu) > 0) {
                    Toast.makeText(getActivity(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                    updateList();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Thêm nhân viên thất bại (Mã nhân viên có thể đã tồn tại)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
