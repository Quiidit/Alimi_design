package com.example.alimi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class main_menu_help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_help);
        getSupportActionBar().setTitle("도움말");
    }
}
