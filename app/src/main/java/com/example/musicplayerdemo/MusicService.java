package com.example.musicplayerdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private int Index = 0;
    private int musicList = 3;
    public MusicService(){
        initMediaPlayer();
    }
    String[] musicIndex = new String[3];
    public void initMediaPlayer(){
        try {
            musicIndex[0] = "/storage/emulated/0/Music/Last Summer.wav";
            musicIndex[1]="/storage/emulated/0/Music/Dawn.mp3";
            musicIndex[2]="/storage/emulated/0/Music/夜的钢琴曲二十八.mp3";
            mediaPlayer.setDataSource(musicIndex[Index]);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playorpause(){

        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }

    public void stop(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void previous() {
        Index--;
        if(Index<=0){
            Toast.makeText(this,"It's the first.",Toast.LENGTH_LONG).show();
        }
        else{
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(musicIndex[Index]);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playorpause();
        }
    }

    public void next() {
        Index++;
        if(Index>=musicList){
            Toast.makeText(this,"It's the last.",Toast.LENGTH_LONG).show();
        }
        else{
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(musicIndex[Index]);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playorpause();
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }
}
