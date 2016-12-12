package com.example.markwen.nfcattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
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

        // Getting the list of events from app directory
        File appDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NFCAttendance/");
        if (!appDirectory.isDirectory()) {
            appDirectory.mkdir();
        }
        String[] fileList = appDirectory.list();
        eventsNameList = new ArrayList<>();
        if (fileList != null){
            for (String aFileList : fileList) {
                if(!aFileList.equals("title_holder.txt")){
                    eventsNameList.add(aFileList.substring(0, aFileList.indexOf(".")));
                }
            }
        }

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
                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
            }
        });
    }
}