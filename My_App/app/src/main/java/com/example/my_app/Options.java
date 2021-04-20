package com.example.my_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Options extends AppCompatActivity {

//    AppConfig appConfig;
    Button perfomTest;
    Button viewRES;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        perfomTest = findViewById(R.id.PerfomTest);
        viewRES = findViewById(R.id.viewResults);

        perfomTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Options.this.appConfig.alertError(Options.this, "Before starting a test session ensure that the IP WebcamServer is running in the background.");
                Toast.makeText(getApplicationContext(),"Before starting a test session ensure that the IP WebcamServer is running in the background.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ConfirmDetails.class);
                startActivity(intent);
            }
        });

        viewRES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewTestResults.class);
                startActivity(intent);
            }
        });
    }
}
