package com.doan.nhatro_kltn.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.doan.nhatro_kltn.Fragment.HomeFragment;
import com.doan.nhatro_kltn.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference UserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*if (currentUser != null){
            Log.d("userid", "Da ton tai ");
        }
        else {
            Log.d("userid", "chua dang nhap ");
        }*/
        UserRef = FirebaseDatabase.getInstance().getReference().child("Account");
        initView();
        loadFragment(new HomeFragment());
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
                {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.menu_maps:
                {
                    Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.menu_add:
                {
                    Toast.makeText(MainActivity.this, "Add post", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.menu_love:
                {
                    Toast.makeText(MainActivity.this, "love's post", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.menu_account:
                {
                    Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
