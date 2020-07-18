package com.doan.timnhatro.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.doan.timnhatro.R;
import com.doan.timnhatro.base.BaseApplication;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.fragment.AccountFragment;
import com.doan.timnhatro.fragment.HouseFragment;
import com.doan.timnhatro.fragment.LikePostFragment;
import com.doan.timnhatro.fragment.MapsFragment;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HouseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        initView();
        loadFragment(new HouseFragment());
        if (AccountUtils.getInstance().getAccount() != null && AccountUtils.getInstance().getAccount().getPhoneNumber().equals(Constants.PHONE_NUMBER_ADMIN)) {
            BaseApplication.addAdminNotificationListener();
        }
    }

    private void initView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    loadFragment(new HouseFragment());
                    return true;
                case R.id.menu_maps:
                    return checkPermission();
                case R.id.menu_add:
                    if (AccountUtils.getInstance().getAccount() != null) {
                        startActivity(new Intent(getApplicationContext(), CreatePostsActivity.class));
                        return false;
                    }
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return false;
                case R.id.menu_account:
                    if (AccountUtils.getInstance().getAccount() != null) {
                        loadFragment(new AccountFragment());
                        return true;
                    }
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return false;
                case R.id.menu_like:
                    if (AccountUtils.getInstance().getAccount() != null) {
                        loadFragment(new LikePostFragment());
                        return true;
                    }
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return false;
            }
            return false;
        }
    };

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
                return false;
            }
        }

        if (LocationUtils.getInstance().isGPSEnable()) {
            loadFragment(new MapsFragment());
            return true;
        }

        new AlertDialog.Builder(HouseActivity.this)
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại", null)
                .show();

        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát Ứng Dụng")
                .setMessage("Bạn có muốn thoát ứng dụng này ngay không ?")
                .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                })
                .setPositiveButton("Trở lại", null)
                .show();
    }

}
