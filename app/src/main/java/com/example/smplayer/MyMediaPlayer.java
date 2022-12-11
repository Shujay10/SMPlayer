package com.example.smplayer;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer instance;
    public static int prevPlayed = -1;
    public static boolean isPlaying = false;

    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;
}
