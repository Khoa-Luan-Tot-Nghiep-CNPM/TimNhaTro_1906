package com.doan.timnhatro.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.Account;
import com.doan.timnhatro.utils.AccountUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends AppCompatActivity {

    private EditText        edtVerifyCode;
    private Account         account;
    private String          verificationId;
    private ProgressDialog  progressDialog;
    private FirebaseAuth    firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        initView();
        getIntentData();
        sendVerifyCode();
    }

    private void sendVerifyCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + account.getPhoneNumber(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(getApplicationContext(), "Gửi mã xác minh thành công", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getIntentData() {
        account = getIntent().getParcelableExtra(Constants.ACCOUNT);
    }

    private void initView() {
        edtVerifyCode = findViewById(R.id.edtVerifyCode);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác thực...");
        progressDialog.setCancelable(false);
    }

    public void onClickLogin(View view) {
        String verifyCode = edtVerifyCode.getText().toString();

        if (TextUtils.isEmpty(verifyCode)){
            Toast.makeText(this, "Mã xác thực không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verifyCode.length() != 6){
            Toast.makeText(this, "Mã xác thực phải có 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verificationId == null){
            Toast.makeText(this, "Mã xác thực chưa được gửi", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verifyCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getAccount();
                        } else {
                            progressDialog.cancel();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Mã xác minh đã nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void getAccount() {
        progressDialog.setMessage("Đang kiểm tra tài khoản...");
        FirebaseDatabase.getInstance().getReference()
                .child("Account")
                .child(account.getUserName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            uploadAvatar();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void uploadAvatar() {
        progressDialog.setMessage("Đang tải lên hình ảnh...");
        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "");

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(account.getAvatar());

            UploadTask uploadTask = firebaseStorage.putStream(inputStream);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return firebaseStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        account.setAvatar(downloadUri.toString());
                        insertAccount();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.cancel();
                    Log.d("dfgf4gfd4s1gdf",e.toString());
                }
            });
        } catch (FileNotFoundException e) {
            progressDialog.cancel();
            e.printStackTrace();
        }
    }

    private void insertAccount() {
        progressDialog.setMessage("Đang tạo tài khoản...");
        FirebaseDatabase.getInstance().getReference()
                .child("Account")
                .child(account.getUserName())
                .setValue(account,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null){
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();

                                    AccountUtils.getInstance().setAccount(account);

                                    Intent intent = new Intent(getApplicationContext(),HouseActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}
