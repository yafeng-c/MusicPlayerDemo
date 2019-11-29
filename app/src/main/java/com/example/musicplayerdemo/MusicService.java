package com.example.musicplayerdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {

    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public MusicService(){
        initMediaPlayer();
    }

    public void initMediaPlayer(){
        try {
            String file_path = "/storage/emulated/0/Music/Last Summer.wav";
            mediaPlayer.setDataSource(file_path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void PlayOrPause(){

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
