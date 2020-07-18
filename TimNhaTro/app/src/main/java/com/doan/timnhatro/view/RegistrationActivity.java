package com.doan.timnhatro.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.Account;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private EditText        edtUsername,edtPassword,edtRePassword,edtName,edtPhoneNumber;
    private String          pathAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initView();
    }

    private void initView() {
        imgAvatar       = findViewById(R.id.imgAvatar);
        edtUsername     = findViewById(R.id.edtUsername);
        edtPassword     = findViewById(R.id.edtPassword);
        edtRePassword   = findViewById(R.id.edtRePassword);
        edtName         = findViewById(R.id.edtName);
        edtPhoneNumber  = findViewById(R.id.edtPhoneNumber);
    }

    public void onClickRegistration(View view) {
        String userName     = edtUsername.getText().toString();
        String password     = edtPassword.getText().toString();
        String rePassword   = edtRePassword.getText().toString();
        String name         = edtName.getText().toString();
        String phoneNumber  = edtPhoneNumber.getText().toString();

        if (pathAvatar == null){
            Toast.makeText(this, "Vui lòng chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Tên tài khoản không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Mật khẩu không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(rePassword)){
            Toast.makeText(this, "Xác thực mật khẩu không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Họ và tên không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Số điện thoại không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)){
            Toast.makeText(this, "Xác thực mật khẩu không đúng !", Toast.LENGTH_SHORT).show();
            edtRePassword.setText("");
            return;
        }

        if (!phoneNumber.startsWith("0")){
            Toast.makeText(this, "Số điện thoại phải bắt đầu bằng số 0 !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() < 10){
            Toast.makeText(this, "Số điện thoại phải có ít nhất 10 chữ số !", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = new Account();
        account.setAvatar(pathAvatar);
        account.setUserName(userName);
        account.setName(name);
        account.setPassword(password);
        account.setPhoneNumber(phoneNumber);

        Intent intent = new Intent(this,VerifyPhoneNumberActivity.class);
        intent.putExtra(Constants.ACCOUNT,account);
        startActivity(intent);
    }

    public void cnClickAvatart(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION);
                return;
            }
        }
        startActivityForResult(new Intent(getApplicationContext(), PicturePickerActivity.class), Constants.REQUEST_CODE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICTURE && resultCode == RESULT_OK && data != null){
            ArrayList<String> arrayPictureResult = data.getStringArrayListExtra(Constants.PICTURE);

            if (arrayPictureResult == null){
                return;
            }

            pathAvatar = arrayPictureResult.get(0);

            Glide.with(this)
                    .load(pathAvatar)
                    .into(imgAvatar);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(getApplicationContext(), PicturePickerActivity.class), Constants.REQUEST_CODE_PICTURE);
            }
        }
    }
}
