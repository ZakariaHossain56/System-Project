package com.example.imagepro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class GoogleAssistant extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_CODE = 123;

    EditText etquery;
    Button btnsend;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_assistant);

        textToSpeech = new TextToSpeech(this, this);

        etquery = findViewById(R.id.etquery);
        btnsend = findViewById(R.id.btnsend);




        //Toast.makeText(getApplicationContext(), "Entered Text: " + query, Toast.LENGTH_SHORT).show();


        // Trigger the Google Assistant with a text query
       // String query = "Your text query here";

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = etquery.getText().toString().trim();
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                speakText(query);
                //sendToGoogleAssistant(query);
            }
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
}
