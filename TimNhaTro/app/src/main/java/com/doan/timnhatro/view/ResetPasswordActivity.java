package com.doan.timnhatro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doan.timnhatro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPass, edtVerifyPass;
    private Button btnChancePass;
    private String phone;
    private DatabaseReference AccountRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        AccountRefs = FirebaseDatabase.getInstance().getReference().child("Account");
        phone = getIntent().getStringExtra("Phone");
       // Toast.makeText(ResetPasswordActivity.this, phone, Toast.LENGTH_SHORT).show();
        edtNewPass = findViewById(R.id.edtNewPass);
        edtVerifyPass = findViewById(R.id.edtVerifyPassword);
        btnChancePass = findViewById(R.id.btnChancePass);
        btnChancePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChancePass();
            }
        });
    }

    private void ChancePass() {

        String newPass = edtNewPass.getText().toString();
        String rePass = edtVerifyPass.getText().toString();

        if (TextUtils.isEmpty(newPass)){
            Toast.makeText(ResetPasswordActivity.this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(rePass) || !newPass.equals(rePass)){
                Toast.makeText(ResetPasswordActivity.this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            }
            else {
                AccountRefs.child(phone).child("password").setValue(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    }
}