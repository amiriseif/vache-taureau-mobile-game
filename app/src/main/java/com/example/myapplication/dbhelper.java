package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dbhelper extends SQLiteOpenHelper {



    public dbhelper( Context context) {
        super(context,"user.sqlite",null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase Mydb) {
        Mydb.execSQL("create Table users(username TEXT primary key,password TEXT,time INT,nbatt INT,glen INT,imgsrc INT)");
        Mydb.execSQL("CREATE TABLE scores (username TEXT PRIMARY KEY, score INTEGER, FOREIGN KEY(username) REFERENCES users(username))");
    }


    @Override
    public void onUpgrade(SQLiteDatabase Mydb, int i, int i1) {
        Mydb.execSQL("drop Table if exists users");

    }
    public Boolean insertData(String username,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("time",1);
        contentValues.put("nbatt",4);
        contentValues.put("glen",4);
        contentValues.put("imgsrc",R.drawable.icon1);

        long result = db.insert("users", null, contentValues);
        if (result == -1) return false;
        else
            return true;
    }




    public void insertScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("score", score);
        long result = db.insert("scores", null, contentValues);

    }
    public void updateScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("score", score);
        db.update("scores", contentValues, "username=?", new String[]{username});
    }

    public void updatetime(int time, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time",time);
        db.update("users", contentValues, "username=?", new String[]{username});
    }
    public int getoldtime(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int time=0;


        Cursor cursor = db.rawQuery("SELECT time FROM users WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            time = cursor.getInt(0);
        }

        cursor.close();
        return time;
    }

    public void updatenbatt(int nbatt,String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nbatt",nbatt);
        db.update("users", contentValues, "username=?", new String[]{username});
    }
    public int getatt(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int att=0;
        Log.d("Username", username);

        Cursor cursor = db.rawQuery("SELECT nbatt FROM users WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            att = cursor.getInt(0);
        }

        cursor.close();
        return att;
    }


    public void updatelen(int len,String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("glen",len);
        db.update("users", contentValues, "username=?", new String[]{username});
    }
    public int getlen(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int len=0;
        Log.d("Username", username);

        Cursor cursor = db.rawQuery("SELECT glen FROM users WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            len = cursor.getInt(0);
        }

        cursor.close();
        return len;
    }
    public void updateicon(String username, int imgsrc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imgsrc", imgsrc);
        db.update("users", contentValues, "username=?", new String[]{username});
    }
    public int geticonsrc(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int src = 0;

        Cursor cursor = db.rawQuery("SELECT imgsrc FROM users WHERE username=?", new String[]{username});

        if (cursor.moveToFirst()) {
            src = cursor.getInt(0);
            Log.d("DBHelper", "Image source retrieved successfully: " + src);
        } else {
            Log.d("DBHelper", "No data found for username: " + username);
        }

        cursor.close();
        return src;
    }




    public Boolean checkusername (String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from users where username = ?", new String[]{username});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public void creatfirestordoc(String username){
        String userid;
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore fstore=FirebaseFirestore.getInstance();

        userid = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userid);
        Map<String,Object> user = new HashMap<>();
        user.put("username", username);
        user.put("score", 0);

        documentReference.set(user);
    }








    public void deleteaccount(String username) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("scores","username=?",new String[]{username});
        db.delete("users","username=?",new String[]{username});
    }


}
