package com.example.markwen.nfcattendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfessorActivity extends AppCompatActivity {

    RecyclerView eventListRecyclerView;
    RecyclerView.Adapter listAdapter;
    RecyclerView.LayoutManager listLayoutManager;
    List<String> eventsNameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        setTitle("Events");

        eventsNameList = new ArrayList<>();
        eventsNameList.add("Test1");
        eventsNameList.add("Test2");

        eventListRecyclerView = (RecyclerView)findViewById(R.id.eventListRecyclerView);
        listAdapter = new RecyclerViewLayoutAdapter(eventsNameList);
        listLayoutManager = new LinearLayoutManager(this);
        eventListRecyclerView.setLayoutManager(listLayoutManager);
    }
}
