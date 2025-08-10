package com.example.amchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String reciverimg, ReciverUid, reciverName, senderUID;
    CircleImageView profile;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderImg;
    public static String reciverIImg;

    String senderRoom, reciverRoom;

    RecyclerView mmessageAdapter;
    ArrayList<msgModelclass> messagessArrayList;
    messagesAdpter messagesAdpter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        // Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // 🔐 Check if user is logged in
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(chatWin.this, login.class)); // Replace with your actual login activity class
            finish();
            return;
        }

        // UI Components
        mmessageAdapter = findViewById(R.id.msgadpter);
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.profilechatt);
        reciverNName = findViewById(R.id.recivernamee);

        // Get Intent Data
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverimg");
        ReciverUid = getIntent().getStringExtra("uid");

        senderUID = firebaseAuth.getUid();
        senderRoom = senderUID + ReciverUid;
        reciverRoom = ReciverUid + senderUID;

        // Recycler View Setup
        messagessArrayList = new ArrayList<>();
        messagesAdpter = new messagesAdpter(chatWin.this, messagessArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessageAdapter.setLayoutManager(linearLayoutManager);
        mmessageAdapter.setAdapter(messagesAdpter);

        // Load profile and name
        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(reciverName);

        // Fetch sender profile image
        DatabaseReference reference = database.getReference().child("user").child(senderUID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue(String.class);
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Fetch messages
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("message");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass message = dataSnapshot.getValue(msgModelclass.class);
                    messagessArrayList.add(message);
                }
                messagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Send message logic
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(chatWin.this, "Please enter message", Toast.LENGTH_SHORT).show();
                    return;
                }

                textmsg.setText("");
                Date date = new Date();
                msgModelclass msgModel = new msgModelclass(message, senderUID, date.getTime());

                database.getReference().child("chats").child(senderRoom).child("message").push()
                        .setValue(msgModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child(reciverRoom).child("message").push()
                                        .setValue(msgModel);
                            }
                        });
            }
        });
    }
}
