package com.example.test2.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test2.DAO.ThuThuDAO;
import com.example.test2.MainActivity;
import com.example.test2.PhieuMuon.PhieuMuonFragment;
import com.example.test2.R;
import com.example.test2.model.ThuThu;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileFragment extends Fragment {
    ImageView imgProfileAvatar;
    TextInputEditText edProfileMaTT, edProfileHoTen, edProfileEmail;
    Button btnUpdateProfile;
    ThuThuDAO dao;
    ThuThu currentThuThu;
    String userLogged;

    // Sử dụng trình chọn ảnh mặc định của Android
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String localPath = saveImageToInternalStorage(uri);
                    if (localPath != null) {
                        imgProfileAvatar.setImageURI(null); // Reset cache hiển thị
                        imgProfileAvatar.setImageURI(Uri.fromFile(new File(localPath)));
                        if (currentThuThu != null) {
                            currentThuThu.setAvatar(localPath);
                        }
                        Toast.makeText(getContext(), "Đã chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private String saveImageToInternalStorage(Uri uri) {
        if (getContext() == null) return null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File file = new File(getContext().getFilesDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        
        imgProfileAvatar = v.findViewById(R.id.imgProfileAvatar);
        edProfileMaTT = v.findViewById(R.id.edProfileMaTT);
        edProfileHoTen = v.findViewById(R.id.edProfileHoTen);
        edProfileEmail = v.findViewById(R.id.edProfileEmail);
        btnUpdateProfile = v.findViewById(R.id.btnUpdateProfile);

        dao = new ThuThuDAO(getContext());
        
        if (getActivity() != null && getActivity().getIntent() != null) {
            userLogged = getActivity().getIntent().getStringExtra("user");
            currentThuThu = dao.getID(userLogged);
        }

        if (currentThuThu != null) {
            edProfileMaTT.setText(currentThuThu.getMaTT());
            edProfileHoTen.setText(currentThuThu.getHoTen());
            edProfileEmail.setText(currentThuThu.getEmail());
            
            if (currentThuThu.getAvatar() != null && !currentThuThu.getAvatar().isEmpty()) {
                File file = new File(currentThuThu.getAvatar());
                if (file.exists()) {
                    imgProfileAvatar.setImageURI(Uri.fromFile(file));
                }
            }
        }

        // Click để chọn ảnh từ thư viện
        imgProfileAvatar.setOnClickListener(view -> mGetContent.launch("image/*"));

        btnUpdateProfile.setOnClickListener(view -> {
            if (currentThuThu == null) return;

            String newHoTen = edProfileHoTen.getText() != null ? edProfileHoTen.getText().toString() : "";
            String newEmail = edProfileEmail.getText() != null ? edProfileEmail.getText().toString() : "";
            
            if (newHoTen.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            currentThuThu.setHoTen(newHoTen);
            currentThuThu.setEmail(newEmail);
            
            if (dao.update(currentThuThu) > 0) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.replaceFragment(new PhieuMuonFragment());
                    mainActivity.setTitle("Quản lý phiếu mượn");
                    mainActivity.updateHeader(); 
                }
            } else {
                Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
