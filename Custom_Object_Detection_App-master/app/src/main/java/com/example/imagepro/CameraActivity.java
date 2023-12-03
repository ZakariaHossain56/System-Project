package com.example.imagepro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.ui.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, TextToSpeech.OnInitListener{
    private static final String TAG="MainActivity";

    FrameLayout theme_cameraview;
    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private objectDetectorClass objectDetectorClass;

    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");

        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    private TextView tvSign, tvsentence;
    private Button backspace, add, space;

    private ImageView buttonSend;
    private static final int REQUEST_CODE = 123;
    private TextToSpeech textToSpeech;

    @Override
    protected void onPostResume() {
        super.onPostResume();

        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_cameraview.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_cameraview.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        textToSpeech = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);

        tvSign = findViewById(R.id.tvSign);
        backspace = findViewById(R.id.backspace);
        add = findViewById(R.id.add);
        space = findViewById(R.id.space);
        tvsentence = findViewById(R.id.tvsentence);
        buttonSend = findViewById(R.id.buttonSend);

        theme_cameraview = findViewById(R.id.theme_cameraview);

        Intent intent = getIntent();
        Object object = intent.getSerializableExtra("object");
        if(object instanceof EachHistory){
            EachHistory history = (EachHistory)object;
            tvsentence.setText(history.getText());
        }


        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        try{
            SharedPreferences sharedPreferences2 = getSharedPreferences("lan",MODE_PRIVATE);
            if(sharedPreferences2.getBoolean("lan",true)){
                SettingsFragment.language = 1;
                objectDetectorClass=new objectDetectorClass(tvSign,backspace,add,space,tvsentence,
                        getAssets(),"hand_model.tflite","custom_label.txt",300,
                        "okkhor.tflite",96
                );
            }
            else{
                SettingsFragment.language = 2;
                objectDetectorClass=new objectDetectorClass(tvSign,backspace,add,space,tvsentence,
                        getAssets(),"hand_model.tflite","custom_label.txt",300,
                        "ASL.tflite",96
                );
            }

            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }

        buttonSend.setOnClickListener((View v)->{
            String text = tvsentence.getText().toString();

            if(text.isEmpty()) return;

            //String query = etquery.getText().toString().trim();
            //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
            saveToDB(text);

            speakText(text);



        });
    }

    private void speakText(String text) {
        if (textToSpeech != null) {
            sendToGoogleAssistant(text);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

                    // You can also check the TTS engine is busy or not
                    while (textToSpeech.isSpeaking()) {
                        // Wait until speaking is finished
                    }
                }
            },2500);
            // Use the TextToSpeech engine to speak the text


            // Once speaking is finished, send the text to Google Assistant

        }
    }

    private void sendToGoogleAssistant(String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Google Assistant not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from the Google Assistant activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the recognized speech text
//            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//
//            // Handle the recognized text as needed
//            String recognizedText = result.get(0);
//            Toast.makeText(this, "Google Assistant recognized: " + recognizedText, Toast.LENGTH_SHORT).show();

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                // Handle the result as needed
                // Note: The user must manually confirm or execute the command in the Google Assistant interface
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    private void saveToDB(String text){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;

        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("history").child(uid);

        LocalDateTime localDateTime  = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, hh:mma");

        String curTime = formatter.format(localDateTime);

        Map<String, Object> map = new HashMap<>();
        map.put("text",text);
        map.put("added_on",curTime);
        ref.push().updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);





    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();
        // Before watching this video please watch previous video of loading tensorflow lite model


        // Rotate the image by 180 degrees
        Core.flip(mRgba, mRgba, 1);
        Core.flip(mGray, mGray, 1);

        // now call that function
        Mat out=new Mat();
        out=objectDetectorClass.recognizeImage(mRgba);

//        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);
//        Imgcodecs.imwrite("output.jpg", mGray); // Save the image to a file
//        System.out.println(System.getProperty("user.dir"));

        return out;
    }

}