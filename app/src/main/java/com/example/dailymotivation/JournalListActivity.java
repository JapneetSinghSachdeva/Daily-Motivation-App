package com.example.dailymotivation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.JournalModel;
import ui.JournalRecyclerAdapter;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<JournalModel> journalModelList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter recyclerAdapter;
    private CollectionReference collectionReference = firestore.collection("Journal");

    private TextView noJournalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        noJournalText = findViewById(R.id.noJournalText);

        journalModelList = new ArrayList<>();

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.addJournal:
                //addjournal
                if(firebaseUser!=null && firebaseAuth!=null)
                {
                    startActivity(new Intent(JournalListActivity.this, PostJournalActivity.class));
                    //finish();
                }
                break;

            case R.id.action_signOut:
                //signout
                if(firebaseUser!=null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this, MainActivity.class));
                    //finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

       // firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

        final JournalModel journalModel = new JournalModel();
        String userID = firebaseUser.getUid();
        journalModel.setUserId(userID);

        collectionReference.whereEqualTo("userId", JournalApi.getInstance().getID())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                if(!queryDocumentSnapshots.isEmpty())
                {
                    for(QueryDocumentSnapshot jour : queryDocumentSnapshots)
                    {
                        //JournalModel models = jour.toObject(JournalModel.class);
                        journalModelList.clear();
                    }


                    for(QueryDocumentSnapshot journals : queryDocumentSnapshots)
                    {
                        JournalModel model = journals.toObject(JournalModel.class);

                        //journalModelList.clear();
                        journalModelList.add(model);

                    }

                    //Setting recyclerView
                    recyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this,
                            journalModelList);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();

                }

                else
                {
                    noJournalText.setVisibility(View.VISIBLE);

                }


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

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
