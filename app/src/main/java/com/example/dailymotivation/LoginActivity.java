package com.example.dailymotivation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import model.JournalModel;
import util.JournalApi;

public class LoginActivity extends AppCompatActivity {

    AppCompatAutoCompleteTextView enterEmail;
    EditText enterPassword;
    Button loginBtn;
    Button signUpBtn;
    ProgressBar loginProgress;

    //Firebase Objects
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth =  FirebaseAuth.getInstance();
        //finding views
        enterPassword = findViewById(R.id.enterPassword);
        enterEmail = findViewById(R.id.enterEmail);
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginProgress = findViewById(R.id.loginProgress);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailData = enterEmail.getText().toString().trim();
                String passwordData = enterPassword.getText().toString().trim();


                loginProgress.setVisibility(View.VISIBLE);
                loginEmailPasswordUser(emailData, passwordData);
            }
        });


    }

    private void loginEmailPasswordUser(String emailData, String passwordData) {

        if(emailData.isEmpty() && passwordData.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "Enter Email \n Enter Password",
                    Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.INVISIBLE);
        }

        else
        {
            firebaseAuth.signInWithEmailAndPassword(emailData,passwordData)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

//                            if(task.isSuccessful())
//                            {


                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            assert user != null;
                            String userID = user.getUid();

                            //getting user on the basis its ID data from database.
                            collectionReference.whereEqualTo("ID", userID) //field name = ID and ID data = userID
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {

                                            //TODO: Handle Exception.

                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                loginProgress.setVisibility(View.INVISIBLE);

                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    JournalApi journalApi = JournalApi.getInstance();

                                                    journalApi.setName(documentSnapshot.getString("user_name"));
                                                    journalApi.setID(documentSnapshot.getString("ID"));

                                                    //to ListActivity
                                                    startActivity(new Intent(LoginActivity.this,
                                                            PostJournalActivity.class));
                                                    finish();

                                                }
                                            }

                                        }
                                    });
                        }

//                            else
//                            {
//                                Toast.makeText(LoginActivity.this, "Please first signUp",
//                                        Toast.LENGTH_SHORT).show();
//                            }

                    //}

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            loginProgress.setVisibility(View.INVISIBLE);
//                            Toast.makeText(LoginActivity.this, "Error \n"+e,
//                                    Toast.LENGTH_SHORT).show();
                            Log.d("Login", "Error: "+e);
                        }
                    });
        }

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        firebaseUser = firebaseAuth.getCurrentUser();
//        firebaseAuth.addAuthStateListener(authStateListener);
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(firebaseAuth!=null)
//        {
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }
//    }


}
