package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    EditText email, password;
    ImageView google;
    Button login;
    dbhelper db;
    String mail, pass, username;
    Intent intent;
    private FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseFirestore fstore;
    GoogleSignInAccount account;
    CheckBox remember;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        google = findViewById(R.id.googlesigin);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.logpass);
        login = findViewById(R.id.login);
        db = new dbhelper(this);
        fstore = FirebaseFirestore.getInstance();
        intent = new Intent(login.this, Gmenu.class);

        remember = findViewById(R.id.rememberMe);
        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkb = preferences.getString("remember", "");
        if (checkb.equals("true")) {
            username = preferences.getString("username", "");
            mail = preferences.getString("email", "");
            intent = new Intent(login.this, Gmenu.class);

            startActivity(intent);
            finish();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("361892729601-jvtar9qql2hetpu021g7fsop56qe3vse.apps.googleusercontent.com")
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = email.getText().toString();
                pass = password.getText().toString();

                if (mail.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(login.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                String userid = user.getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userid);
                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (value != null && value.exists()) {
                                            username = value.getString("username");

                                            signIn();
                                        } else {
                                            Toast.makeText(login.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(login.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isChecked()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember", "true");
                            editor.putString("username", username);
                            editor.putString("email", mail);
                            editor.apply();
                            Toast.makeText(login.this, "checked", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember", "false");
                            editor.apply();
                            Toast.makeText(login.this, "unchecked", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(login.this, "Google sign in failed: ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        mail = user.getEmail();
                        username = retriveusername(account.getEmail());

                        String userId = user.getUid();
                        DocumentReference documentReference = fstore.collection("users").document(userId);
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // User document exists
                                        username = document.getString("username");
                                    } else {
                                        db.creatfirestordoc(username);
                                    }
                                    saveCredentialsAndSignIn();
                                } else {
                                    Toast.makeText(login.this, "Failed to check user profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveCredentialsAndSignIn() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "true");
        editor.putString("username", username);
        editor.putString("email", mail);
        editor.apply();

        signIn();
    }

    private void signIn() {
        Toast.makeText(login.this, "Login succeeded!", Toast.LENGTH_SHORT).show();
        intent = new Intent(login.this, Gmenu.class);


        if (!db.checkusername(mail)) {
            db.insertData(mail, pass);
        }
        startActivity(intent);
        finish();
    }

    private String retriveusername(String mail) {
        String ch = "";
        for (int i = 0; i < mail.length(); ++i) {
            ch = ch + mail.charAt(i);
            if (mail.charAt(i) == '@') {
                break;
            }
        }
        return ch.substring(0, ch.length() - 1);
    }

    public void signup(View view) {
        startActivity(new Intent(this, register.class));
        finish();
    }

    public void resetpassword(View view) {
        mail = email.getText().toString();
        if (mail.isEmpty()) {
            Toast.makeText(login.this, "Write your email in the email field!", Toast.LENGTH_SHORT).show();
        } else {
            auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(login.this, "If an email is registered with this account, a reset email will be sent!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(login.this, "Email not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
