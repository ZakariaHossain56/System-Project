package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class playVideoActivity extends AppCompatActivity {

    EditText editText;
    private String txt;
    int id = 0 ;
    StyledPlayerView playerView;
    ExoPlayer player;
    String[] tokens1;
    private Button txtToSignBtn,cancel;
    SharedPreferences videoRef;// = getSharedPreferences("videoRef",MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


       // String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                txt= null;
            } else {
                txt= extras.getString("txt");
            }
        } else {
            txt= (String) savedInstanceState.getSerializable("txt");
        }
        tokens1 = txt.split(" ");
        playerView = findViewById(R.id.playerView);
        cancel = findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
            }
        });
        videoRef = getSharedPreferences("videoRef",MODE_PRIVATE);
        show();

        //dialog.show();



    }
    public void show()
    {
        if(id==tokens1.length){finish();return;}
        String token  = tokens1[id];

        SharedPreferences tokenUsed = getSharedPreferences("tokenUsed", MODE_PRIVATE);
        SharedPreferences.Editor editor = tokenUsed.edit();
        int currentCount = tokenUsed.getInt(token, 0);


            // Toast.makeText(textToSign.this, token, Toast.LENGTH_SHORT).show();
            String path = videoRef.getString(tokens1[id]+".mp4",null);
            if(path == null) {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                id = id+1;
                show();
                //continue;
            }
            else {
                editor.putInt(token,currentCount+1);
                editor.apply();
              //  Toast.makeText(this, tokens1[id]+"ddbaal  " + path, Toast.LENGTH_SHORT).show();

                //StyledPlayerView playerView = findViewById(R.id.playerView);
                player = new ExoPlayer.Builder(playVideoActivity.this).build();
                playerView.setPlayer(player);
                //MediaItem mediaItem = MediaItem.fromUri(localFile.getAbsolutePath());
                if (videoRef.contains(tokens1[id]+".mp4")) {
                    MediaItem mediaItem = MediaItem.fromUri(videoRef.getString(tokens1[id]+".mp4",null));
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.setPlayWhenReady(true);
                    // Use the path as needed
                } else {
                    Toast.makeText(this, "doesn't exist", Toast.LENGTH_SHORT).show();
                    // Handle the case where the key is not present
                }



                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == Player.STATE_ENDED) {
                            // Player has reached the end of the video
                            // You can perform any action you want, e.g., close the activity
                            id = id + 1;
                            show();
                            //String nextVideoPath = getNextVideoPath();


                        }
                    }

                    @Override
                    public void onPlayerError(PlaybackException error) {
                        // Handle playback errors if needed
                    }
                });

            }
        return;

    }
}