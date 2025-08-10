package com.example.amchat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private RecyclerView userRecyclerView;
    private UserAdpter userAdapter;
    private ArrayList<Users> usersList;
    private ImageView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // If user is not logged in, go to login screen
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, login.class));
            finish();
            return;
        }

        usersList = new ArrayList<>();
        userRecyclerView = findViewById(R.id.mainUserRecyclerView);
        logoutBtn = findViewById(R.id.logoutimg);

        userAdapter = new UserAdpter(MainActivity.this, usersList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);

        // Fetch user list from Firebase
        database.getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Users user = data.getValue(Users.class);
                    if (!auth.getCurrentUser().getUid().equals(user.getUserId())) {
                        usersList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Logout logic
        logoutBtn.setOnClickListener(view -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialoge);
        dialog.setContentView(R.layout.dialog_layout);

        Button yesBtn = dialog.findViewById(R.id.yesbtn);
        Button noBtn = dialog.findViewById(R.id.nobtn);

        yesBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, login.class));
            finish();
        });

        noBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
