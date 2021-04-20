package com.example.my_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewTestResults extends AppCompatActivity {
    Button viewVid;
    Button buttonGetToken;

    TextView temp;
    TextInputEditText textInputLayoutTKNum;
    TextInputEditText textInputLayoutTaskScore1;
    TextInputEditText textInputLayoutTaskScore2;
    TextInputEditText textInputLayoutTaskScore3;
    TextInputEditText textInputLayoutTaskScore4;
    TextInputEditText textInputLayoutTaskScore5;
    private String vidUrl;
    AppConfig appConfig;
    TextInputEditText textInputLayoutTaskID1;
    TextInputEditText textInputLayoutTaskID2;
    TextInputEditText textInputLayoutTaskID3;
    TextInputEditText textInputLayoutTaskID4;
    TextInputEditText textInputLayoutTaskID5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_test_results);

        viewVid = findViewById(R.id.buttonViewVid);
        buttonGetToken = findViewById(R.id.buttonGetToken);
        textInputLayoutTKNum = findViewById(R.id.token);
        textInputLayoutTaskScore1 = findViewById(R.id.TaskScore1);
        textInputLayoutTaskScore2 = findViewById(R.id.TaskScore2);
        textInputLayoutTaskScore3 = findViewById(R.id.TaskScore3);
        textInputLayoutTaskScore4 = findViewById(R.id.TaskScore4);
        textInputLayoutTaskScore5 = findViewById(R.id.TaskScore5);
        textInputLayoutTaskID1 = findViewById(R.id.TaskID1);
        textInputLayoutTaskID2 = findViewById(R.id.TaskID2);
        textInputLayoutTaskID3 = findViewById(R.id.TaskID3);
        textInputLayoutTaskID4 = findViewById(R.id.TaskID4);
        textInputLayoutTaskID5 = findViewById(R.id.TaskID5);
        temp = findViewById(R.id.temp);
        appConfig = new AppConfig();

        viewVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrieviewTestVideo.class);
                intent.putExtra("vidUrl", temp.getText());
                startActivity(intent);
            }
        });

        buttonGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tokenNum;
                tokenNum = String.valueOf(textInputLayoutTKNum.getText());

                if(!tokenNum.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[1];
                            field[0] = "tokenNum";
                            //Creating array for data
                            String[] data = new String[1];
                            data[0] = tokenNum;

                            PutData putData = new PutData("http://192.168.43.216/HASS_AUTONOMOUS_DRIVER_LICENCE_TESTING/LoginRegister/GetDriverData.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(!result.equals("Token does not exist")){
                                        appConfig.alertError(ViewTestResults.this ,"Getting Results");
                                        viewVid.setVisibility(View.VISIBLE);
                                        try {
                                            loadIntoListView(result);
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        appConfig.alertError(ViewTestResults.this ,"No Record Found For Token.");
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    appConfig.alertError(ViewTestResults.this ,"Enter all fields");
                }
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
        temp.setText(jsonArray.getJSONObject(0).getString("VideoPath"));
        textInputLayoutTaskID1.setText(jsonArray.getJSONObject(0).getString("Task1ID"));
        textInputLayoutTaskScore1.setText(jsonArray.getJSONObject(0).getString("Score1Points"));
        textInputLayoutTaskID2.setText(jsonArray.getJSONObject(0).getString("Task2ID"));
        textInputLayoutTaskScore2.setText(jsonArray.getJSONObject(0).getString("Score2Points"));
        textInputLayoutTaskID3.setText(jsonArray.getJSONObject(0).getString("Task3ID"));
        textInputLayoutTaskScore3.setText(jsonArray.getJSONObject(0).getString("Score3Points"));
        textInputLayoutTaskID4.setText(jsonArray.getJSONObject(0).getString("Task4ID"));
        textInputLayoutTaskScore4.setText(jsonArray.getJSONObject(0).getString("Score4Points"));
        textInputLayoutTaskID5.setText(jsonArray.getJSONObject(0).getString("Task5ID"));
        textInputLayoutTaskScore5.setText(jsonArray.getJSONObject(0).getString("Score5Points"));

    }
}
