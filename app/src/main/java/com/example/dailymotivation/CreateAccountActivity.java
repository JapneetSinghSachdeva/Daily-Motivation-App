package com.example.dailymotivation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.Intent;
import android.os.Bundle;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.JournalModel;
import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    ProgressBar create_actProgress;
    EditText create_actUsername, create_actPassword;
    AppCompatAutoCompleteTextView create_actEmail;
    Button create_actSignUpBtn;
    //Firebase Objects
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        create_actEmail = findViewById(R.id.create_actEmail);
        create_actPassword = findViewById(R.id.create_actPassword);
        create_actUsername = findViewById(R.id.create_actUsername);
        create_actSignUpBtn = findViewById(R.id.create_actSignUpBtn);
        create_actProgress = findViewById(R.id.create_actProgress);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser!=null)
            {
                //user is logged in.
            }

            else
            {
                //no user.
            }

            }
        };

        create_actSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userNameData = create_actUsername.getText().toString();
                String emailData = create_actEmail.getText().toString();
                String passwordData = create_actPassword.getText().toString();

                if(userNameData.isEmpty() && emailData.isEmpty() && passwordData.isEmpty())
                {
                    Toast.makeText(CreateAccountActivity.this,
                            "Empty fields are not allowed!",
                            Toast.LENGTH_LONG).show();
                }


                else
                {
                    createUserAccount(userNameData,emailData,passwordData);
                }


            }
        });


    }

    private void createUserAccount(final String user_name, String email_data, String password_data)
    {
        if(user_name.isEmpty() && email_data.isEmpty() && password_data.isEmpty())
        {
            Toast.makeText(CreateAccountActivity.this,
                    "Empty fields are not allowed!", Toast.LENGTH_LONG)
                    .show();

        }

        else
        {
            create_actProgress.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email_data, password_data)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //We take user to addJournalActivity.
                                firebaseUser = firebaseAuth.getCurrentUser();

                                assert firebaseUser != null;
                                final String userID = firebaseUser.getUid();

                                //create a user map so that we can create UserMap in firestore Db.
                                Map<String,String> userMap = new HashMap<>();
                                userMap.put("ID", userID);
                                userMap.put("user_name", user_name);

                                //saving this to firestore database.

                                collectionReference.add(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                if(Objects.requireNonNull(task.getResult()).exists())
                                                                {
                                                                     create_actProgress.setVisibility(View.INVISIBLE);
                                                                     String name = task.getResult().getString("user_name");
                                                                     Intent intent = new Intent(CreateAccountActivity.this,
                                                                             PostJournalActivity.class);

                                                                     //Global API
                                                                    JournalModel journalModel = new JournalModel();
                                                                    JournalApi journalApi = JournalApi.getInstance();
                                                                    journalApi.setID(userID);
                                                                    journalApi.setName(user_name);



                                                                     intent.putExtra("user_name", name);
                                                                     intent.putExtra("userId", userID);
                                                                     startActivity(intent);
                                                                     finish();
                                                                }

                                                                else
                                                                {
                                                                    create_actProgress.setVisibility(View.INVISIBLE);

                                                                }

                                                            }
                                                        });

                                            }
                                        })
                                        //for collection Reference.
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }

                            else{
                                //something went wrong!


                            }


                        }
                    })
                    //forFirebaseAuth
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            create_actProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateAccountActivity.this, "Error" +e, Toast.LENGTH_LONG).show();
                            Log.d("Main", "Error"+e);

                        }
                    });


        }




    }



    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}
