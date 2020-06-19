package com.doan.timnhatro.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doan.timnhatro.R;
import com.doan.timnhatro.model.Account;
import com.doan.timnhatro.utils.AccountUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText edtPassword,edtRePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        initView();
        createToolbar();
    }

    private void initView() {
        edtPassword     = findViewById(R.id.edtPassword);
        edtRePassword   = findViewById(R.id.edtRePassword);
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_32dp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    public void onClickUpdatePassword(View view) {
        String password = edtPassword.getText().toString();
        String rePassword = edtRePassword.getText().toString();

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Mật khẩu không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(rePassword)){
            Toast.makeText(this, "Xác thực mật khẩu không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)){
            Toast.makeText(this, "Xác thực mật khẩu không chính xác !", Toast.LENGTH_SHORT).show();
            edtRePassword.setText("");
            return;
        }

        final Account account = AccountUtils.getInstance().getAccount();
        account.setPassword(password);

        FirebaseDatabase.getInstance().getReference()
                .child("Account")
                .child(account.getUserName())
                .setValue(account, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            AccountUtils.getInstance().setAccount(account);
                            Toast.makeText(getApplicationContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
