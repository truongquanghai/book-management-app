package com.example.test2.ChangePass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.DAO.ThuThuDAO;
import com.example.test2.R;
import com.example.test2.model.ThuThu;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePassFragment extends Fragment {
    TextInputEditText edPassOld, edPass, edRePass;
    Button btnSave, btnCancel;
    ThuThuDAO dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_pass, container, false);
        edPassOld = v.findViewById(R.id.edPassOld);
        edPass = v.findViewById(R.id.edPass);
        edRePass = v.findViewById(R.id.edRePass);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        dao = new ThuThuDAO(getActivity());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPassOld.setText("");
                edPass.setText("");
                edRePass.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passOld = edPassOld.getText().toString();
                String pass = edPass.getText().toString();
                String rePass = edRePass.getText().toString();
                
                if (validate() > 0) {
                    // Lấy user từ Intent của MainActivity
                    String user = getActivity().getIntent().getStringExtra("user");
                    ThuThu thuThu = dao.getID(user);
                    if (thuThu != null) {
                        thuThu.setMatKhau(pass);
                        if (dao.updatePass(thuThu) > 0) {
                            Toast.makeText(getActivity(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            edPassOld.setText("");
                            edPass.setText("");
                            edRePass.setText("");
                        } else {
                            Toast.makeText(getActivity(), "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return v;
    }

    public int validate() {
        int check = 1;
        if (edPassOld.getText().toString().isEmpty() || edPass.getText().toString().isEmpty() || edRePass.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            check = -1;
        } else {
            // Kiểm tra mật khẩu cũ
            String user = getActivity().getIntent().getStringExtra("user");
            String passOld = edPassOld.getText().toString();
            if (!dao.checkLogin(user, passOld)) {
                Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                check = -1;
            }
            // Kiểm tra mật khẩu mới và nhập lại
            String pass = edPass.getText().toString();
            String rePass = edRePass.getText().toString();
            if (!pass.equals(rePass)) {
                Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                check = -1;
            }
        }
        return check;
    }
}
