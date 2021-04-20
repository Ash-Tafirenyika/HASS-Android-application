package com.example.my_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class ConfirmDetails extends AppCompatActivity {

    public String res;
    TextInputEditText textInputLayoutTokenNum;
    TextInputEditText textInputLayoutVehicleID;
    Button buttonVerify;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);

        textInputLayoutTokenNum = findViewById(R.id.tokenNumber);
        textInputLayoutVehicleID = findViewById(R.id.vehicleID);
        buttonVerify = findViewById(R.id.buttonVerify);

        OnBackPressedCallback callback = new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed(){
                finish();;
            }
        };
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tokenNum, vehicleID;
                tokenNum = String.valueOf(textInputLayoutTokenNum.getText());
                vehicleID = String.valueOf(textInputLayoutVehicleID.getText());

                if(!tokenNum.equals("") && !vehicleID.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "vehicleID";
                            field[1] = "tokenNum";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = vehicleID;
                            data[1] = tokenNum;

                            PutData putData = new PutData("http://192.168.43.216/HASS_AUTONOMOUS_DRIVER_LICENCE_TESTING/LoginRegister/confirmtoken.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(!result.equals("Token does not exist")){
                                        Toast.makeText(getApplicationContext(), "Token found", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), VerifyDriverInfo.class);
                                        intent.putExtra("Token", tokenNum);
                                        intent.putExtra("result", result);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Token does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

        public String getResult(String result){
            return result;
        }
}
