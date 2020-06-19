package com.doan.timnhatro.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.doan.timnhatro.R;
import com.doan.timnhatro.model.Account;
import com.doan.timnhatro.utils.AccountUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void onClickRegistration(View view) {
        startActivity(new Intent(this,RegistrationActivity.class));
    }

    public void onClickLogin(View view) {
        String userName = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Tên tài khoản không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Mật khẩu không được để trống !", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference()
                .child("Account")
                .child(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);

                            if (account.getPassword().equals(password)){

                                AccountUtils.getInstance().setAccount(account);

                                Intent intent = new Intent(getApplicationContext(),HouseActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(), "Sai mật khẩu !", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Tài khoản không tồn tại !", Toast.LENGTH_SHORT).show();
                            edtPassword.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
