package com.example.my_app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("MissingPermission")
public class MainActivity extends Activity implements SensorEventListener{

    private static final String TAG = "HASS_ERROR_MSG" ;
//    String[] options = {"1080p","720p","480p"};
//    String[] options1 = {"15 Hz","10 Hz"};
//    String[] options2 = {"10 fps","20 fps","30 fps"};
//    private Camera mCamera;
//    private CameraPreview mPreview;
    private AppConfig appConfig;
//    private MediaRecorder mediaRecorder;
    private ImageButton capture, vid;
//    private Context myContext;
//    private FrameLayout cameraPreview;
    private Chronometer chrono;

    Location location;
    LocationManager lm;
    double latitude = 0;
    double longitude = 0;
    double accuracy = 0;
    double latitude_original = 0;
    double longitude_original = 0;
    float distance = 0;
    float speed = 0;
    float dist[] = {0,0,0};
//    PrintWriter writer = null;
//    public Camera.Parameters param;
//    public Camera.Size size;
//    long timechecker = 5000;
//    byte[] frame;

    private TextView tv;
    private TextView txt;
    //------------------------------------------------------------
    public Socket socketClient;
    public BufferedReader in;
    public BufferedWriter out;
    private TextView mylongt;
    private TextView mylat;
    private TextView myspeed;
    private TextView mytoken;
    private TextView mydist;
    private TextView myaccuracy;
    JSONObject DataObj = new JSONObject();
    JSONObject dataOut = new JSONObject();
//    JSONObject linear_accall = new JSONObject();
//    JSONObject accel_accall = new JSONObject();
    JSONArray jsonArray = null;
    Criteria criteria = new Criteria();
//    String[] accel;
//    String[] lin;

    public String DriverToken;
    boolean connection = false;
    boolean sessionEnd = false;
    boolean recording = false;
    boolean sending = false;
    //=============================================================
//    int quality = 0;
    int rate = 100;
//    String timeStampFile;
//    int clickFlag = 0;
    Timer timer;
//    int VideoFrameRate = 24;
//    byte[] byteFrame;

    private SensorManager sensorManager;
//    private Sensor accelerometer;
//    private Sensor linear_accel;
    private Sensor head;
//    private Sensor gyro;
    private Sensor rotv;
//    public DataOutputStream outStream;

//    float linear_acc_x = 0;
//    float linear_acc_y = 0;
//    float linear_acc_z = 0;
//    float accelo_acc_x = 0;
//    float accelo_acc_y = 0;
//    float accelo_acc_z = 0;
    float heading = 0.0f;
//    float gyro_x = 0;
//    float gyro_y = 0;
//    float gyro_z = 0;
    float rotv_x = 0;
    float rotv_y = 0;
    float rotv_z = 0;
    float rotv_w = 0;
    float rotv_accuracy = 0;

    LocationListener locationListener;
    LocationManager LM;
//    Camera.PreviewCallback myPrev = new Camera.PreviewCallback() {
//        @Override
//        public void onPreviewFrame(byte[] data, Camera camera) {
//            try {
//                YuvImage yuv_image = new YuvImage(data, param.getPreviewFormat(), size.width, size.height, null); // all bytes are in YUV format therefore to use the YUV helper functions we are putting in a YUV object
//                Rect rect = new Rect(0, 0, size.width, size.height);
//                ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
//                yuv_image.compressToJpeg(rect, 50, output_stream);// image has now been converted to the jpg format and bytes have been written to the output_stream object
//                byte[] tmp = output_stream.toByteArray();//getting the byte array
//		        MainActivity.this.byteFrame = tmp;
//                System.gc();
//                Log.d(TAG, "onPreviewFrame - wrote bytes: " + data.length);
//		        Log.d(TAG, "onPreviewFrame - wrote bytes: " + tmp.length);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        myContext = this;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        connect();
//        this.linear_accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.head = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//        this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.rotv = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.capture = findViewById(R.id.button_capture);
        this.capture.setOnClickListener(captureListener);
        this.chrono = findViewById(R.id.chronometer);
        this.appConfig = new AppConfig();
        this.txt = findViewById(R.id.txt1);
        this.txt.setTextColor(-16711936);
        this.vid = findViewById(R.id.imageButton);
        this.vid.setVisibility(View.GONE);
        this.tv = findViewById(R.id.textViewHeading);
        this.mylat = findViewById(R.id.lat);
        this.mylongt = findViewById(R.id.longit);
        this.myspeed = findViewById(R.id.txtspeed);
        this.mytoken = findViewById(R.id.txtToken);
        this.mydist = findViewById(R.id.txtdistance);
        this.myaccuracy = findViewById(R.id.txtaccuracy);

        if (getIntent().hasExtra("Token")) {
            this.DriverToken = (Objects.requireNonNull(getIntent().getExtras())).getString("Token");
        }
        else{
            this.DriverToken = "ERROR GETTING TOKEN";
            this.appConfig.alertError(MainActivity.this, this.DriverToken);
        }
//        Log.i("mysetup ", String.valueOf(this.outStream));
//        this.cameraPreview = findViewById(R.id.camera_preview);
//        this.mPreview = new CameraPreview(myContext, mCamera);
//        this.cameraPreview.addView(this.mPreview);
    }

//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//    private int findBackFacingCamera() {
//        int cameraId = -1;
//        // Search for the back facing camera
//        // get the number of cameras
//        int numberOfCameras = Camera.getNumberOfCameras();
//        // for every camera check
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                cameraId = i;
//                break;
//            }
//        }
//        return cameraId;
//    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void onResume() {
        super.onResume();
//        if (!checkCameraHardware(this.myContext)) {
//            Toast toast = Toast.makeText(myContext, "Phone doesn't have a camera!", Toast.LENGTH_LONG);
//            toast.show();
//            finish();
//        }
//        if (!checkCameraHardware(myContext)) {
//            Toast toast = Toast.makeText(myContext, "Phone doesn't have a camera!", Toast.LENGTH_LONG);
//            toast.show();
//            finish();
//        }
//        if (this.mCamera == null) {
//            this.mCamera = Camera.open(findBackFacingCamera());
//            this.mPreview.refreshCamera(this.mCamera);
//            this.mCamera.setPreviewCallback(myPrev);
//            this.mCamera.startPreview();// camera frame preview starts when user launches application screen
//            param = this.mCamera.getParameters();// acquire the parameters for the camera
//            size = param.getPreviewSize();// get the size of each frame captured by the camera
//        }

//        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.sensorManager.registerListener(this, this.head, SensorManager.SENSOR_DELAY_GAME);
//        this.sensorManager.registerListener(this, this.gyro, SensorManager.SENSOR_DELAY_NORMAL);
        this.sensorManager.registerListener(this, this.rotv, SensorManager.SENSOR_DELAY_NORMAL);

        this.locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                MainActivity.this.latitude  = location.getLatitude();
                MainActivity.this.longitude = location.getLongitude();
                MainActivity.this.accuracy = location.getAccuracy();

                if(location.hasSpeed()) {
                    MainActivity.this.speed = location.getSpeed();

                }
                Location.distanceBetween(MainActivity.this.latitude_original, MainActivity.this.longitude_original,
                        MainActivity.this.latitude, MainActivity.this.longitude, MainActivity.this.dist);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Acquire a reference to the system Location Manager
        this.LM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        this.criteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.LM.getBestProvider(this.criteria, true);
        // Register the listener with the Location Manager to receive location updates
        this.LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
//        releaseCamera();
        this.sensorManager.unregisterListener(this);

    }

//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }

    View.OnClickListener captureListener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public void onClick(View v) {

            if (MainActivity.this.recording) {
                // stop recording and release camera
//                MainActivity.this.mediaRecorder.stop(); // stop the recording
//                releaseMediaRecorder(); // release the MediaRecorder object
//                Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
//                MainActivity.this.recording = false;
                sending = false;
                MainActivity.this.chrono.stop();
                MainActivity.this.chrono.setBase(SystemClock.elapsedRealtime());

                MainActivity.this.chrono.start();
                MainActivity.this.chrono.stop();
                MainActivity.this.txt.setTextColor(-16711936);
//                enddata();
                MainActivity.this.sessionEnd = true;
                MainActivity.this.appConfig.alertError(MainActivity.this,"Session Terminated!");

                try {
                    MainActivity.this.socketClient.close();
                } catch (IOException | NullPointerException e) {
                    MainActivity.this.appConfig.alertError(MainActivity.this, e.getMessage());
                }
                Log.d("socket", "closed ");

            } else {

//                MainActivity.this.timeStampFile = String.valueOf((new Date()).getTime());
//                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/HASS/");
//                wallpaperDirectory.mkdirs();

//                File wallpaperDirectory1 = new File(Environment.getExternalStorageDirectory().getPath()+"/HASS/"
//                        +MainActivity.this.timeStampFile);
//                wallpaperDirectory1.mkdirs();
                Toast.makeText(getApplicationContext(),"Processing...", Toast.LENGTH_SHORT).show();
//                storeData();

                LocationManager original = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location original_location = original.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(original.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
                    MainActivity.this.latitude_original = original_location.getLatitude();
                    MainActivity.this.longitude_original = original_location.getLongitude();
                }
                timer = new Timer();
                timer.schedule(new SayHello(), 0, MainActivity.this.rate);
                MainActivity.this.chrono.setBase(SystemClock.elapsedRealtime());
                MainActivity.this.chrono.start();
                MainActivity.this.txt.setTextColor(-65536);
                MainActivity.this.recording = true;

            }
        }
    };

//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    private void releaseMediaRecorder() {
//        if (this.mediaRecorder != null) {
//            this.mediaRecorder.reset(); // clear recorder configuration
//            this.mediaRecorder.release(); // release the recorder object
//            this.mediaRecorder = null;
//            this.mCamera.lock(); // lock camera for later use
//        }
//    }


//    private void releaseCamera() {
//        // stop and release camera
//        if (this.mCamera != null) {
//
//            this.mCamera.setPreviewCallback(null);
//            this.mCamera.release();
//            this.mCamera = null;
//
//        }
//    }

    /* --------------------- Data Section ----------------------------*/

    class SayHello extends TimerTask {

        @SuppressLint("SimpleDateFormat")
        public void run() {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.getBestProvider(MainActivity.this.criteria, true);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

//            if (MainActivity.this.writer != null){MainActivity.this.writer.println(MainActivity.this.longitude_original + "," + MainActivity.this.latitude_original + ","
//                    + MainActivity.this.speed + "," + MainActivity.this.dist[0] + "," + new SimpleDateFormat("HH-mm-ss").format(new Date())
//                    + "," + MainActivity.this.linear_acc_x + "," + MainActivity.this.linear_acc_y + "," + MainActivity.this.linear_acc_z + "," +
//                    MainActivity.this.heading + "," + MainActivity.this.gyro_x + "," + MainActivity.this.gyro_y + "," + MainActivity.this.gyro_z);
//            }

            try {
                MainActivity.this.dataOut.put("TOKEN",MainActivity.this.DriverToken);
                MainActivity.this.dataOut.put("TIMES_TAMP", new SimpleDateFormat("mm-ss-ms").format(new Date()));

                MainActivity.this.dataOut.put("SPEED", MainActivity.this.speed);
                MainActivity.this.dataOut.put("DISTANCE", MainActivity.this.dist[0]);
                MainActivity.this.dataOut.put("LAT_LOCATION", MainActivity.this.latitude_original);
                MainActivity.this.dataOut.put("LONG_LOCATION", MainActivity.this.longitude_original);
                MainActivity.this.dataOut.put("HEADING", MainActivity.this.heading);
                MainActivity.this.dataOut.put("GPS_ACCURACY", MainActivity.this.accuracy);
//                MainActivity.this.dataOut.put("LINEAR_ACCEL", String.valueOf(MainActivity.this.linear_accall));
//                MainActivity.this.dataOut.put("ACCELOR_Z", String.valueOf(MainActivity.this.accel_accall));
            } catch (JSONException ejs) {
                MainActivity.this.appConfig.alertError(MainActivity.this,ejs.getMessage());
            }

            MainActivity.this.jsonArray = new JSONArray();
            MainActivity.this.jsonArray.put(MainActivity.this.dataOut);

            try {
                MainActivity.this.DataObj.put("SensorData", MainActivity.this.jsonArray);
                if(!sending){
                    SendData();
                    sending = true;
                }
                Log.i("byte data : ", String.valueOf(MainActivity.this.DataObj.toString().length()));

            } catch (JSONException | NullPointerException ero) {
                Log.i("Error : ===> ",ero.getMessage());
            }
        }
    }

//    public void storeData() {
//
//        String filePath = Environment.getExternalStorageDirectory().getPath()+"/HASS/" + MainActivity.this.timeStampFile + "/" +
//                MainActivity.this.timeStampFile + ".csv";
//
//        try {
//            MainActivity.this.writer = new PrintWriter(filePath);
//            MainActivity.this.writer.println("Longitude,Latitude,Speed,Distance,Time,Acc X,Acc Y,Acc Z,Heading,gyro_x,gyro_y,gyro_z");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.i("error ","writing to phone");
//        }
//
//        LocationManager original = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Location original_location = original.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if(original.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
//            MainActivity.this.latitude_original = original_location.getLatitude();
//            MainActivity.this.longitude_original = original_location.getLongitude();
//        }
//
//        timer = new Timer();
//        timer.schedule(new SayHello(), 0, this.rate);
//    }

//    public void enddata() {
//        if  (MainActivity.this.writer != null){
//        MainActivity.this.writer.close();}
//    }
    /* ---------------------- Sensor data ------------------- */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if(event.sensor.getType() == 2) {
//            MainActivity.this.rotv_x = event.values[0];
//            MainActivity.this.rotv_y = event.values[1];
//            MainActivity.this.rotv_z = event.values[2];
//            MainActivity.this.rotv_w = event.values[3];
//            MainActivity.this.rotv_accuracy = event.values[4];
//        }

//        if(event.sensor.getType() == 10) {
//            MainActivity.this.linear_acc_x = event.values[0];
//            MainActivity.this.linear_acc_y = event.values[1];
//            MainActivity.this.linear_acc_z = event.values[2];
//            lin = new String[]{String.valueOf(MainActivity.this.linear_acc_x), String.valueOf(MainActivity.this.linear_acc_y),
//                    String.valueOf(MainActivity.this.linear_acc_z)};
//            try {
//                MainActivity.this.linear_accall.put("LIN_ACC", lin);
//            } catch (JSONException e) {
//                Log.i("Failed Lin acc"," get data");
//            }
//        }
//        if(event.sensor.getType() == 9) {
//            MainActivity.this.accelo_acc_x = event.values[0];
//            MainActivity.this.accelo_acc_y = event.values[1];
//            MainActivity.this.accelo_acc_z = event.values[2];
//            accel = new String[]{String.valueOf(MainActivity.this.accelo_acc_x), String.valueOf(MainActivity.this.accelo_acc_y),
//                    String.valueOf(MainActivity.this.accelo_acc_z)};
//            try {
//                MainActivity.this.accel_accall.put("ACCELO_ACC",accel);
//            } catch (JSONException e) {
//                Log.i("Failed Accello acc"," get data");
//            }
//        }
        if (event.sensor.getType() == 3) {
            MainActivity.this.heading = Math.round(event.values[0]);
            if(MainActivity.this.heading >= 270){
                MainActivity.this.heading = MainActivity.this.heading + 90;
                MainActivity.this.heading = MainActivity.this.heading - 360;
            }
            else{
                MainActivity.this.heading = MainActivity.this.heading + 90;
            }
        }
//        else if (event.sensor.getType() == 4){
//            MainActivity.this.gyro_x = event.values[0];
//            MainActivity.this.gyro_y = event.values[1];
//            MainActivity.this.gyro_z = event.values[2];
//        }
        String myheading = "Heading: " + MainActivity.this.heading;
        String loc_accuracy = "Accuracy: " + MainActivity.this.accuracy;
        String mydistance = "distace: " + MainActivity.this.dist[0]+"m";
        String my_loc_lat = "latitude: " + MainActivity.this.latitude;
        String my_loc_longi = "longitude: " + MainActivity.this.longitude;
        String my_loc_speed = "speed: " + MainActivity.this.speed+"m/s";
        MainActivity.this.mydist.setText(mydistance);
        MainActivity.this.mylat.setText(my_loc_lat);
        MainActivity.this.mylongt.setText( my_loc_longi);
        MainActivity.this.myspeed.setText( my_loc_speed);
        MainActivity.this.myaccuracy.setText(loc_accuracy);
        MainActivity.this.mytoken.setText(MainActivity.this.DriverToken);
        MainActivity.this.tv.setText(myheading);
    }

//    public void addQuality(View view){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        String setting = new String();
//        if(quality == 0) {
//            setting = "1080p";
//        }
//        else if(quality == 1){
//            setting = "720p";
//        }
//        else if(quality == 2){
//            setting = "480p";
//        }
//        builder.setTitle("Pick Quality, Current setting: " + setting)
//                .setItems(options, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                        if(which == 0){
//                            quality = 0;
//                        }
//                        else if (which == 1){
//                            quality = 1;
//                        }
//                        else if (which == 2){
//                            quality = 2;
//                        }
//                    }
//                });
//        builder.show();
//    }
//    public void addRate(View view)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        String setting = new String();
//        if(rate == 100) {
//            setting = "10 Hz";
//        }
//        else if(rate == 67){
//            setting = "15 Hz";
//        }
//        builder.setTitle("Pick Data Save Rate, Current setting: " + setting)
//                .setItems(options1, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                        if(which == 0){
//                            rate = 67 ;
//                        }
//                        else if (which == 1){
//                            rate = 100;
//                        }
//                    }
//                });
//        builder.show();
//    }
//    public void addFrameRate(View view)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        String setting = new String();
//        if(VideoFrameRate == 10) {
//            setting = "10 fps";
//        }
//        else if(VideoFrameRate == 20){
//            setting = "20 fps";
//        }
//        else if(VideoFrameRate == 30){
//            setting = "30 fps";
//        }
//        builder.setTitle("Pick Video fps, Current setting: " + setting)
//                .setItems(options2, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                        if(which == 0){
//                            VideoFrameRate = 10 ;
//                        }
//                        else if (which == 1){
//                            VideoFrameRate = 20;
//                        }
//                        else if (which == 2){
//                            VideoFrameRate = 30;
//                        }
//                    }
//                });
//        builder.show();
//    }

    public void connect() {
//        final boolean[] vidsoc = {false};
//        final boolean[] datasoc = {false};
        new Thread(new Runnable(){

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                if (MainActivity.this.socketClient == null){
//                    while (true){
                    try {
//                        if (!vidsoc[0]) {
                            MainActivity.this.socketClient = new Socket("192.168.43.216", 15555);
//                            vidsoc[0] = true;
//                        }
//                        if (!datasoc[0]){
//                            MainActivity.this.socketClient = new Socket("192.168.43.216", 15555);
////                            MainActivity.this.socketClientSens = new Socket("192.168.43.20", 15550);
//                            datasoc[0] = true;
//                        }
//                        if (vidsoc[0]){
//                            if (datasoc[0]){
                            MainActivity.this.out = new BufferedWriter(new OutputStreamWriter(MainActivity.this.socketClient.getOutputStream()));
//                                MainActivity.this.outStream = new DataOutputStream(MainActivity.this.socketClient.getOutputStream());
                            MainActivity.this.connection = true;
                            Log.i("SOCKET CREATED : ", " READY ......");
                            MainActivity.this.out.write(MainActivity.this.DriverToken);
                            MainActivity.this.out.flush();
//                                break;}
//                        }
//                        MainActivity.this.in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                    } catch (final Exception err) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                MainActivity.this.appConfig.alertError(MainActivity.this,err.getMessage());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void SendData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (MainActivity.this.connection) {//checks for connection to server

                    try {
                        while (!sessionEnd) {
//                            MainActivity.this.outStream.write(MainActivity.this.byteFrame.length);// sending the size of the array
//                            MainActivity.this.outStream.write(MainActivity.this.byteFrame);// writing the array to the socket output stream
//                            MainActivity.this.outStream.write(MainActivity.this.DataObj);
//                            MainActivity.this.outStream.flush();
                            MainActivity.this.out.write(String.valueOf(MainActivity.this.DataObj.toString().length()));
                            MainActivity.this.out.write(String.valueOf(MainActivity.this.DataObj));
                            MainActivity.this.out.flush();
                            Log.i("DATA SENT LENGTH", String.valueOf(MainActivity.this.DataObj.toString().length()));
                        }
                    } catch (Exception e) {
                        Log.i("ERROR SENDING : ", e.getMessage());
                    }
                }
            }
        }).start();
    }
}