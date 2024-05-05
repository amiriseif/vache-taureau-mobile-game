package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ranking extends AppCompatActivity {
    TextView w1, w2, w3;
    TextView w1score, w2score, w3score;
    TextView myrank;
    dbhelper db;
    Intent intent2;
    String username;


    List<String> top3list;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        w1 = findViewById(R.id.w1username);
        w2 = findViewById(R.id.w2username);
        w3 = findViewById(R.id.w3username);
        myrank = findViewById(R.id.myranks);


        db = new dbhelper(this);



        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        userid=fauth.getCurrentUser().getUid();
        DocumentReference documentReference=fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                Long userScore = value.getLong("score");
                myrank.setText(String.valueOf(userScore));


            }
        });
        getTop3Scores();



    }
    private void getTop3Scores() {
        Task<QuerySnapshot> queryTask = fstore.collection("users")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(3)
                .get();

        queryTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    top3list = new ArrayList<>();
                    int index = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        String username = document.getString("username");
                        Long score = document.getLong("score");
                        if (score != null) {
                            top3list.add(username + ": " + score);
                            switch (index) {
                                case 0:
                                    w1.setText(username + ": " + score);
                                    break;
                                case 1:
                                    w2.setText(username + ": " + score);
                                    break;
                                case 2:
                                    w3.setText(username + ": " + score);
                                    break;
                            }
                            index++;
                        }
                    }
                } else {
                    Toast.makeText(ranking.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }











}
