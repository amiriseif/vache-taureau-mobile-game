package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class game extends AppCompatActivity {

    EditText input;
    Button check;
    ListView lv;
    TextView cscore,bscore,time;
    ArrayList <String>list;
    Random rand;
    dbhelper db ;
    String email;
    String username;
    Intent intent2;
    CountDownTimer timer;
    int score=1000;
    int att=1;
    int rindex;
    int duration,len,nbatt;
    String x="";
    ArrayList<Integer> list1;
    String tries, lvi;
    long min;
    long second;
    FirebaseFirestore fstore;
    String sscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        cscore=findViewById(R.id.cscore);
        bscore=findViewById(R.id.bscore);
        time=findViewById(R.id.timer);
        input=findViewById(R.id.userin);
        check=findViewById(R.id.check);
        lv=findViewById(R.id.list);
        list=new ArrayList<String>();
        list1=new ArrayList<Integer>();
        db=new dbhelper(this);
        intent2 = getIntent();
        username = intent2.getStringExtra("username");
        email = intent2.getStringExtra("email");
        duration=db.getoldtime(email);
        nbatt=db.getatt(email);
        len=db.getlen(email);
        fstore = FirebaseFirestore.getInstance();



        startTime();

        for (int i=0; i<10; ++i){
            list1.add(i);
        }
        rand=new Random();
        for (int j=0; j<len; ++j){
            rindex =Integer.valueOf(rand.nextInt(list1.size()));
            x= list1.get(rindex)+x;
            list1.remove(rindex);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(att<=nbatt){
                tries=input.getText().toString();
                if(tries.length()==len){
                tries=input.getText().toString();
                score=score -10*att;
                att++;
                cscore.setText((String.valueOf(cowcheck(tries, x))));
                bscore.setText((String.valueOf(bullcheck(tries, x))));
                //Toast.makeText(game.this, x, Toast.LENGTH_SHORT).show();
                lvi=tries+"   "+"cow="+String.valueOf(cowcheck(tries, x))+"   "+"bull="+String.valueOf(bullcheck(tries, x));
                list.add(lvi);
                lv.setAdapter(arrayAdapter);

                if(tries.equals(x)) {
                    timer.cancel();
                    score= (int) ((score*(1/duration)+min*100+second*10)*len);

                    db.updateScore(email,score);
                    sscore = String.valueOf(score);
                    updatescore(username,score);

                    AlertDialog.Builder builder = new AlertDialog.Builder(game.this);
                    builder.setMessage("correct answer your score is:\n"+String.valueOf(score));
                    builder.setTitle("game finish");
                    builder.setCancelable(false);
                    builder.setPositiveButton("new game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            input.getText().clear();
                            recreate();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }else{
                    Toast.makeText(game.this,"check your guess length!",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(game.this);
                    builder.setMessage("you finished your attempt! you lose the game");
                    builder.setTitle("game finish");
                    builder.setCancelable(false);
                    builder.setPositiveButton("new game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            input.getText().clear();
                            recreate();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    timer.cancel();
                }
            }
        });
    }

    private void updatescore(String username, int score) {
        Map<String ,Object> userdetail=new HashMap<>();
        userdetail.put("score", score);
        fstore.collection("users").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                    String documentId=documentSnapshot.getId();
                    fstore.collection("users").document(documentId).update(userdetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(game.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(game.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(game.this, "No user found with the username: " + username, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTime() {
        timer=new CountDownTimer(duration*60000,1000) {
            @Override
            public void onTick(long l) {
                min=((l/1000)%3600)/60;
                second=(l/1000)%60;
                String timeformat=String.format(Locale.getDefault(),"%02d:%02d",min,second);
                time.setText(timeformat);
            }

            @Override
            public void onFinish() {
                AlertDialog.Builder builder = new AlertDialog.Builder(game.this);
                builder.setMessage("time has finish you lose the game");
                builder.setTitle("game finish");
                builder.setCancelable(false);
                builder.setPositiveButton("new game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        input.getText().clear();
                        recreate();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        timer.start();
    }

    public int cowcheck(String userin,String randomn){
        int i=0;
        for (char ch: userin.toCharArray()) {
            if (randomn.contains(String.valueOf(ch)) && !(userin.indexOf(String.valueOf(ch))==randomn.indexOf(String.valueOf(ch))))
                i++;
        }
        return i;
    }

    public int bullcheck(String userin,String randomn){
        int i=0;
        for (char ch: userin.toCharArray()) {
            if (userin.indexOf(String.valueOf(ch))==randomn.indexOf(String.valueOf(ch)))
                i++;
        }
        return i;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
