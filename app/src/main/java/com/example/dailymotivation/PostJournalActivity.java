package com.example.dailymotivation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

import model.JournalModel;
import util.JournalApi;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1 ;
    private ImageView camera_image_button, journalBackground;
    private TextView userName_textView, date_textView;
    private EditText title_et, thoughts_et;
    private Button save_journalBtn;
    ProgressBar progressBar;

    private String currentUserId;
    private String currentUserName;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Connecting to Fiestore.
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //Storage
    private StorageReference storageReference;

    private CollectionReference collectionReference = firestore.collection("Journal");
    private Uri imageUri;
    private String tag = "Post";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //finding views.
        progressBar = findViewById(R.id.progressBar);
        camera_image_button = findViewById(R.id.camera_image_button);
        userName_textView = findViewById(R.id.userName_textView);
        date_textView = findViewById(R.id.date_textView);
        title_et = findViewById(R.id.title_et);
        thoughts_et = findViewById(R.id.thoughts_et);
        save_journalBtn = findViewById(R.id.save_journalBtn);
        journalBackground = findViewById(R.id.journalBackground);

        progressBar.setVisibility(View.INVISIBLE);

        //onClick
        camera_image_button.setOnClickListener(this);
        save_journalBtn.setOnClickListener(this);

        if(JournalApi.getInstance() != null)
        {
            currentUserId = JournalApi.getInstance().getID(); //getting userID
            currentUserName = JournalApi.getInstance().getName(); //getting userName

            userName_textView.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null)
                {

                }

                else
                {

                }


            }
        };


    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.camera_image_button:
                //get image from gallery.
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            break;

            case R.id.save_journalBtn:
                //saveButton
                saveJournal();
                break;
        }
    }

    private void saveJournal()
    {
        final String title = title_et.getText().toString().trim();
        final String thoughts = thoughts_et.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(title.isEmpty() && thoughts.isEmpty() && imageUri==null)
        {
            Toast.makeText(PostJournalActivity.this, "Enter title \n Enter Thoughts", Toast.LENGTH_SHORT)
                    .show();
            title_et.setError("enter title");
            thoughts_et.setError("enter thoughts");

        }
        else
        {
            final StorageReference filePath = storageReference  //in this we are storing the path of images
                    .child("journal_images") // path--> journal_images/my_image_8874043.
                    .child("my_image_"+ Timestamp.now().getSeconds()); // need to have unique image names.

            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //progressBar.setVisibility(View.INVISIBLE);

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //Journal Object Set.
                                    String imageUrl = uri.toString();
                                    JournalModel journalModel = new JournalModel();
                                    journalModel.setImageUrl(imageUrl);
                                    journalModel.setThought(thoughts);
                                    journalModel.setTitle(title);
                                    journalModel.setUserName(currentUserName);
                                    journalModel.setTimeAdded(new Timestamp(new Date()));
                                    //getting current userID
                                    JournalApi journalApi = JournalApi.getInstance();
                                    String id = journalApi.getID();

                                    journalModel.setUserId(id);

                                    //Invoking collectionReference
                                    collectionReference.add(journalModel)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(PostJournalActivity.this,
                                                            JournalListActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.d(tag, "Error: "+e);
                                                }
                                            });


                                }
                            });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostJournalActivity.this, "Error: \n"+e, Toast.LENGTH_SHORT)
                                    .show();

                            Log.d("Main", "Error: "+e);


                        }
                    });

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode== RESULT_OK)
        {
            if(data!=null)
            {
                imageUri = data.getData();//actual path
                journalBackground.setImageURI(imageUri);//showing image.

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
