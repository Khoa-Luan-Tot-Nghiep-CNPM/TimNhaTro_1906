package com.doan.timnhatro.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.timnhatro.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edtPhone, edtVerifyCode;
    private TextView txtVerifyCode;
    private Button btnVerifyCode, btnResetPass;
    private DatabaseReference AccountRefs;
    private String VerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        AccountRefs = FirebaseDatabase.getInstance().getReference().child("Account");

        edtPhone = findViewById(R.id.edtNumberPhone);
        edtVerifyCode = findViewById(R.id.edtVerifyCodePhone);
        btnVerifyCode = findViewById(R.id.btnVerifyPhone);
        btnResetPass = findViewById(R.id.btnResetpass);
        txtVerifyCode = findViewById(R.id.txtVerifyCode);

        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCodePhone();
            }
        });

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verifyCode = edtVerifyCode.getText().toString();

                if (TextUtils.isEmpty(verifyCode)){
                    Toast.makeText(ForgetPasswordActivity.this, "Mã xác thực không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (verifyCode.length() != 6){
                    Toast.makeText(ForgetPasswordActivity.this, "Mã xác thực phải có 6 chữ số", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (VerificationId == null){
                    Toast.makeText(ForgetPasswordActivity.this, "Mã xác thực chưa được gửi", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("Phone", edtPhone.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void sendCodePhone() {

        if (TextUtils.isEmpty(edtPhone.getText())){
            Toast.makeText(ForgetPasswordActivity.this, "Vui lòng nhập số điện thoại của bạn!", Toast.LENGTH_SHORT).show();
        }
        else {
            AccountRefs.child(edtPhone.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        Toast.makeText(ForgetPasswordActivity.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        txtVerifyCode.setVisibility(View.VISIBLE);
                        edtVerifyCode.setVisibility(View.VISIBLE);
                        btnResetPass.setVisibility(View.VISIBLE);
                        receiverCode();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void receiverCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + edtPhone.getText().toString(),
                30L /*timeout*/,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationId = verificationId;
                        Toast.makeText(getApplicationContext(), "Gửi mã xác minh thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        edtVerifyCode.setText(phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(getApplicationContext(), "Hạn ngạch SMS cho dự án đã bị vượt quá", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}