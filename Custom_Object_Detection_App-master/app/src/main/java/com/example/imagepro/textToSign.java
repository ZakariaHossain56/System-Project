//
//
//
//package com.example.imagepro;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import android.widget.Toolbar;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.ui.StyledPlayerView;
//
//
//public class textToSign extends AppCompatActivity {
//
//    EditText editText;
//    private String txt;
//    StyledPlayerView playerView;
//    ExoPlayer player;
//    private Button txtToSignBtn,cancel;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_text_to_sign);
//        editText = findViewById(R.id.edit_text);
//        txtToSignBtn = findViewById(R.id.submit_button);
//        txtToSignBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txt = editText.getText().toString();
//                Intent i = new Intent(textToSign.this, playVideoActivity.class);
//                String strName = null;
//                i.putExtra("txt",txt);
//                startActivity(i);
//
//                String[] tokens1 = txt.split(" ");
//
//                //Toast.makeText(textToSign.this, String.valueOf(tokens1.length), Toast.LENGTH_LONG).show();
//
//
//// Now, 'tokens' array contains individual words
//
//                editText.setText("");
//                Dialog dialog=new Dialog(textToSign.this);
//                dialog.setContentView(R.layout.show_video_dialog);
//                playerView = dialog.findViewById(R.id.playerView);
//                cancel = dialog.findViewById(R.id.Cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                for (String token : tokens1) {
//                    SharedPreferences videoRef = getSharedPreferences("videoRef",MODE_PRIVATE);
//                    Toast.makeText(textToSign.this, token, Toast.LENGTH_SHORT).show();
//                    String path = videoRef.getString(token+".mp4",null);
//                    if(path == null) continue;
//
//                    //StyledPlayerView playerView = findViewById(R.id.playerView);
//                    player = new ExoPlayer.Builder(textToSign.this).build();
//                    playerView.setPlayer(player);
//                    //MediaItem mediaItem = MediaItem.fromUri(localFile.getAbsolutePath());
//                    MediaItem mediaItem = MediaItem.fromUri(path);
//                    player.setMediaItem(mediaItem);
//                    player.prepare();
//                    player.setPlayWhenReady(true);
//                }
//                dialog.show();
//
//
//            }
//        });
//
//
//        //Toolbar toolbar = findViewById(R.id.toolbar);
//        //setActionBar(toolbar);
//
//    }
//}