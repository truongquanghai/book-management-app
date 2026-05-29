package com.example.test2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.test2.AddUser.AddUserFragment;
import com.example.test2.ChangePass.ChangePassFragment;
import com.example.test2.DangXuat.DangXuatFragment;
import com.example.test2.DoanhThu.DoanhThuFragment;
import com.example.test2.LoaiSach.LoaiSachFragment;
import android.net.Uri;
import com.example.test2.PhieuMuon.PhieuMuonFragment;
import com.example.test2.DAO.ThuThuDAO;
import com.example.test2.Profile.ProfileFragment;
import com.example.test2.Sach.SachFragment;
import com.example.test2.ThanhVien.ThanhVienFragment;
import com.example.test2.Top10.Top10Fragment;
import com.example.test2.model.ThuThu;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView nv;
    ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        nv = findViewById(R.id.nvView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Mặc định mở Phiếu mượn khi vào
        replaceFragment(new PhieuMuonFragment());

        // Hiển thị user và xử lý Profile trên Header
        View headerView = nv.getHeaderView(0);
        TextView tvUser = headerView.findViewById(R.id.tvUser);
        TextView tvEmail = headerView.findViewById(R.id.tvEmail);
        imgAvatar = headerView.findViewById(R.id.imgAvatar);

        String user = getIntent().getStringExtra("user");
        
        // Lấy thông tin thực tế từ Database
        ThuThuDAO ttDAO = new ThuThuDAO(this);
        ThuThu currentTT = ttDAO.getID(user);
        
        if (currentTT != null) {
            tvUser.setText("Chào mừng " + currentTT.getHoTen() + "!");
            tvEmail.setText(currentTT.getEmail());
            // Hiển thị ảnh đại diện nếu có
            if (currentTT.getAvatar() != null && !currentTT.getAvatar().isEmpty()) {
                File file = new File(currentTT.getAvatar());
                if (file.exists()) {
                    imgAvatar.setImageURI(Uri.fromFile(file));
                }
            }
        } else {
            tvUser.setText("Chào mừng " + user + "!");
        }
        
        // Sự kiện chuyển sang trang Profile
        View.OnClickListener toProfile = v -> {
            replaceFragment(new ProfileFragment());
            drawer.closeDrawers();
            setTitle("Hồ sơ cá nhân");
        };
        imgAvatar.setOnClickListener(toProfile);
        tvUser.setOnClickListener(toProfile);
        tvEmail.setOnClickListener(toProfile);

        // Phân quyền
        if (user != null && !user.equalsIgnoreCase("admin")) {
            nv.getMenu().findItem(R.id.nav_AddUser).setVisible(false);
        }

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_PhieuMuon) {
                    replaceFragment(new PhieuMuonFragment());
                } else if (id == R.id.nav_LoaiSach) {
                    replaceFragment(new LoaiSachFragment());
                } else if (id == R.id.nav_Sach) {
                    replaceFragment(new SachFragment());
                } else if (id == R.id.nav_ThanhVien) {
                    replaceFragment(new ThanhVienFragment());
                } else if (id == R.id.nav_Top10) {
                    replaceFragment(new Top10Fragment());
                } else if (id == R.id.nav_DoanhThu) {
                    replaceFragment(new DoanhThuFragment());
                } else if (id == R.id.nav_AddUser) {
                    replaceFragment(new AddUserFragment());
                } else if (id == R.id.nav_ChangePass) {
                    replaceFragment(new ChangePassFragment());
                } else if (id == R.id.nav_Logout) {
                    replaceFragment(new DangXuatFragment());
                }

                drawer.closeDrawers();
                setTitle(item.getTitle());
                return true;
            }
        });
    }

    public void updateHeader() {
        View headerView = nv.getHeaderView(0);
        TextView tvUser = headerView.findViewById(R.id.tvUser);
        TextView tvEmail = headerView.findViewById(R.id.tvEmail);
        imgAvatar = headerView.findViewById(R.id.imgAvatar);

        String user = getIntent().getStringExtra("user");
        ThuThuDAO ttDAO = new ThuThuDAO(this);
        ThuThu currentTT = ttDAO.getID(user);

        if (currentTT != null) {
            tvUser.setText("Chào mừng " + currentTT.getHoTen() + "!");
            tvEmail.setText(currentTT.getEmail());
            if (currentTT.getAvatar() != null && !currentTT.getAvatar().isEmpty()) {
                File file = new File(currentTT.getAvatar());
                if (file.exists()) {
                    imgAvatar.setImageURI(null); // Reset để ép buộc làm mới
                    imgAvatar.setImageURI(Uri.fromFile(file));
                }
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .commit();
    }
}
