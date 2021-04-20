package com.example.my_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUp extends AppCompatActivity {
    Button signup, buttonLgin;
    TextInputEditText textInputLayoutDPassword, textInputLayoutDUsername,textInputLayoutDId ;
    ProgressBar progressbar;
    AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup = findViewById(R.id.buttonSignUp);
        buttonLgin = findViewById(R.id.buttonLOgin);
        textInputLayoutDId = findViewById(R.id.DriverID);
        textInputLayoutDUsername = findViewById(R.id.Dusername);
        textInputLayoutDPassword = findViewById(R.id.Dpassword);
        progressbar = findViewById(R.id.dprogress);
        appConfig = new AppConfig();

        buttonLgin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username, password, dID;
                username = String.valueOf(textInputLayoutDUsername.getText());
                password = String.valueOf(textInputLayoutDPassword.getText());
                dID = String.valueOf(textInputLayoutDId.getText());
                if(!username.equals("") && !password.equals("") && !dID.equals("")) {
                progressbar.setVisibility(View.VISIBLE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Starting Write and Read data with URL
                        //Creating array for parameters
                        String[] field = new String[3];
                        field[0] = "DriverID";
                        field[1] = "Dusername";
                        field[2] = "Dpassword";
                        //Creating array for data
                        String[] data = new String[3];
                        data[0] = dID;
                        data[1] = username;
                        data[2] = password;

                        PutData putData = new PutData("http://192.168.43.216/HASS_AUTONOMOUS_DRIVER_LICENCE_TESTING/LoginRegister/signup.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                progressbar.setVisibility(View.GONE);
                                appConfig.alertError(SignUp.this , result);
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
                }
                else{
                    appConfig.alertError(SignUp.this ,"All fields are required");
                }
            }
        });
    }
}

