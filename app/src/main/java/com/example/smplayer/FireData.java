package com.example.smplayer;

public class FireData {

    int index;
    boolean isPlaying;
    boolean isPaused;

    public FireData() {
    }

    public FireData(int index, boolean isPlaying, boolean isPaused) {
        this.index = index;
        this.isPlaying = isPlaying;
        this.isPaused = isPaused;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}
