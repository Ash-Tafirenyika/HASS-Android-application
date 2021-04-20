package com.example.my_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerifyDriverInfo extends AppCompatActivity {
        AppConfig appConfig;
        //ListView DriverInfo;
        TextInputEditText textInputLayoutLLR;
        TextInputEditText textInputLayoutDate;
        TextInputEditText textInputLayoutPhone;
        TextInputEditText textInputLayoutClass;
        TextInputEditText textInputLayoutGender;
        TextInputEditText textInputLayoutName;

        TextView tokenNum;
        Button buttonConfirm;
        public ImageView img;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_verify_driver_info);

            buttonConfirm = findViewById(R.id.buttonConfirm);
            tokenNum = findViewById(R.id.tokenNum);
            textInputLayoutLLR = findViewById(R.id.LLR);
            textInputLayoutDate = findViewById(R.id.bookDate);
            textInputLayoutPhone = findViewById(R.id.bookTime);
            textInputLayoutClass = findViewById(R.id.classType);
            textInputLayoutGender = findViewById(R.id.gender);
            textInputLayoutName = findViewById(R.id.Name);
            img = findViewById(R.id.imageView);
            appConfig = new AppConfig();

            OnBackPressedCallback callback = new OnBackPressedCallback(true){
                @Override
                public void handleOnBackPressed(){
                    finish();;
                }
            };
            if(getIntent().hasExtra("Token")){
                String myToken = (getIntent().getExtras()).getString("Token");
                tokenNum.setText(myToken);
            }
            if(getIntent().hasExtra("result")){
                String result = (getIntent().getExtras()).getString("result");
                try {
                    loadIntoListView(result);
                } catch (JSONException e) {

                    Log.i("Failed at Json", "get result");
                }
            }


            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Token", tokenNum.getText());
                    startActivity(intent);
                    appConfig.alertError(VerifyDriverInfo.this ,"Verified Successfully.");
                }
            });
        }


        private void getJSON(final String urlWebService) {

            @SuppressLint("StaticFieldLeak")
            class GetJSON extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    try {
                        loadIntoListView(s);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        URL url = new URL(urlWebService);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        StringBuilder sb = new StringBuilder();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String json;
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json).append("\n");
                        }
                        return sb.toString().trim();
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            GetJSON getJSON = new GetJSON();
            getJSON.execute();
        }
        @SuppressLint("SetTextI18n")
        private void loadIntoListView(String json) throws JSONException {
            Log.i("Data : ", json);
            JSONArray jsonArray = new JSONArray(json);
            textInputLayoutName.setText(jsonArray.getJSONObject(0).getString("fname") + " " +
                    jsonArray.getJSONObject(0).getString("lname"));
            textInputLayoutLLR.setText(jsonArray.getJSONObject(0).getString("LicenceNum"));
            textInputLayoutDate.setText(jsonArray.getJSONObject(0).getString("BookingDate"));
            textInputLayoutPhone.setText(jsonArray.getJSONObject(0).getString("DphoneNum"));
            textInputLayoutClass.setText(jsonArray.getJSONObject(0).getString("licenceClass"));
            textInputLayoutGender.setText(jsonArray.getJSONObject(0).getString("DGender"));
            Picasso.with(getBaseContext()).load(jsonArray.getJSONObject(0).getString("Dpicture")).into(img);
        }

    }

