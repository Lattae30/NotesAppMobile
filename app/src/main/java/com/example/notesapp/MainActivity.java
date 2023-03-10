package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    RecyclerView recycleView;
    DatabaseReference dbNotes;
    EditText searchInput;
    SearchView searchView;
    ImageView addAction, searchIcon;
    NotesAdapter notesAdapter;

    // Untuk menampung data yang terdapat di class Notes
    ArrayList<Notes> notesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        recycleView = findViewById(R.id.recycleView);
        addAction = findViewById(R.id.imageAddAction);

        // WHEN BUTTON ADD CLICKED
        addAction.setOnClickListener(this);

        // CONNECT WITH DATABASE TO GET THE REFERENCE DATA
        dbNotes = FirebaseDatabase.getInstance().getReference("notes");
        notesArrayList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // SET THE CARD NOTES
        dbNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notesArrayList.clear();

                // DataSnapshot contain data from database.
                // Any time read the data on database, will receive the data as data snapshot.
                for(DataSnapshot notesSnapshot : snapshot.getChildren()){
                    // Getting the value from the notes class
                    Notes notes = notesSnapshot.getValue(Notes.class);
                    // Menyimpan data ke dalam array
                    notesArrayList.add(notes);
                }
                // Access class notes adapter
                notesAdapter = new NotesAdapter(MainActivity.this);
                // Set array data ke dalam Noteslist class NotesAdapter
                notesAdapter.setNotesList(notesArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                recycleView.setLayoutManager(layoutManager);
                recycleView.setLayoutManager(
                        // spanCount: 2 means that there will be 2 grids
                        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                );

                // SET RECYCLE VIEW ATAU CARD
                recycleView.setAdapter(notesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // SEARCH SECTION
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String sch) {
                    search(sch);
                    return true;
                }
            });
        }
    }

    // SEARCH METHOD
    private void search(String str){
        ArrayList<Notes> searchList = new ArrayList<>();
        for(Notes object : notesArrayList){
            if(object.getTitle().toLowerCase().contains(str.toLowerCase())
                || object.getSubtitle().toLowerCase().contains(str.toLowerCase())){
                searchList.add(object);
            }
        }
        NotesAdapter adapterClass = new NotesAdapter(MainActivity.this);
        adapterClass.setNotesList(searchList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        recycleView.setAdapter(adapterClass);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageAddAction) {
            Intent intent = new Intent(MainActivity.this, create_activity.class);

            Toast.makeText(this, "Press back button to save notes", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    //Back Navigation
    public void back(View view)
    {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getText(R.string.click_again), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                doubleBackToExitPressedOnce = true; }
        }, 1000);
    }
}