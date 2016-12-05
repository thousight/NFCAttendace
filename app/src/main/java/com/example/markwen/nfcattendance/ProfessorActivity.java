package com.example.markwen.nfcattendance;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ProfessorActivity extends AppCompatActivity {

    RecyclerView eventListRecyclerView;
    RecyclerViewLayoutAdapter listAdapter;
    LinearLayoutManager listLayoutManager;
    List<String> eventsNameList;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        setTitle("Events");

        eventsNameList = new ArrayList<>();
        eventsNameList.add("Test1");
        eventsNameList.add("Title");

        eventListRecyclerView = (RecyclerView)findViewById(R.id.eventListRecyclerView);
        listAdapter = new RecyclerViewLayoutAdapter(eventsNameList);
        eventListRecyclerView.setAdapter(listAdapter);

        // Setting LayoutManager
        listLayoutManager = new LinearLayoutManager(this);
        listLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventListRecyclerView.setLayoutManager(listLayoutManager);

        // FAB
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }
}
