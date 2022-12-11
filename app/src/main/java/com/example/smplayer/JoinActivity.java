package com.example.smplayer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JoinActivity extends AppCompatActivity {

    private ArrayList<AudioModel> songsList = new ArrayList<>();

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x=0;

    FirebaseDatabase database;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();

        titleTv = findViewById(R.id.song_title1);
        currentTimeTv = findViewById(R.id.current_time1);
        totalTimeTv = findViewById(R.id.total_time1);
        seekBar = findViewById(R.id.seek_bar1);
        pausePlay = findViewById(R.id.pause_play1);
        nextBtn = findViewById(R.id.next1);
        previousBtn = findViewById(R.id.previous1);
        musicIcon = findViewById(R.id.music_icon_big1);

        titleTv.setSelected(true);
        takeSongs();

        JoinActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        //musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        //musicIcon.setRotation(0);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void getData(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FireData fire = dataSnapshot.getValue(FireData.class);
                MyMediaPlayer.currentIndex = fire.getIndex();

                if(MyMediaPlayer.prevPlayed == -1){
                    MyMediaPlayer.prevPlayed = fire.getIndex();
                }else if(MyMediaPlayer.prevPlayed != fire.getIndex() ){
                    mediaPlayer.reset();
                    MyMediaPlayer.prevPlayed = fire.getIndex();
                }

                currentSong = songsList.get(MyMediaPlayer.currentIndex);
                titleTv.setText(currentSong.getTitle());
                totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

                // TODO : Here is the change
                //if(fire.isPlaying()){
                playMusic(fire.isPaused,fire.isPlaying());
//                }else {
//                    pausePlay();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mRef.child("Host").addValueEventListener(postListener);

    }

    private void playMusic(boolean flag,boolean flag2){

        if(!flag){
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepare();
                if(flag2)
                    mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            pausePlay(flag2);
        }

    }

    private void pausePlay(boolean isPly){
        if(mediaPlayer.isPlaying() && !isPly){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.start();
        }

    }

    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }


    private void takeSongs(){

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        getData();
    }
}