package com.example.musicplayerdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button isPlay;
    private Button stop;
    private Button pause;

    private TextView totalTime;
    private TextView playingTime;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindServiceConnection();
        musicService = new MusicService();

        isPlay = (Button) findViewById(R.id.Play);
        isPlay.setOnClickListener(new myOnClickListener());

        stop = (Button) findViewById(R.id.Stop);
        stop.setOnClickListener(new myOnClickListener());

        pause = (Button) findViewById(R.id.Pause);
        pause.setOnClickListener(new myOnClickListener());

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
        seekBar.setMax(musicService.mediaPlayer.getDuration());

        totalTime = (TextView) findViewById(R.id.totalTime);
        playingTime = (TextView) findViewById(R.id.playingTime);

    }

    private MusicService musicService;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;

        }
    };

    private void bindServiceConnection(){
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent,sc,this.BIND_AUTO_CREATE);
    }

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            isPlay.setOnClickListener(new myOnClickListener());
            stop.setOnClickListener(new myOnClickListener());
            pause.setOnClickListener(new myOnClickListener());

            playingTime.setText(time.format(musicService.mediaPlayer.getCurrentPosition()));
            totalTime.setText(time.format(musicService.mediaPlayer.getDuration()));
            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        musicService.mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });

            handler.postDelayed(runnable, 100);

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(isApplicationBroughtToBackground()){
            Log.e("b","On the background");
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {

        verifyStoragePermission(this);
        seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
        seekBar.setMax(musicService.mediaPlayer.getDuration());
        handler.post(runnable);
        super.onResume();
        Log.d("hint","handler post runnable");

    }

    private class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.Play:
                    changePlay();
                    musicService.PlayOrPause();
                    break;
                case R.id.Stop:
                    musicService.stop();
                    changeStop();
                    break;
                default:
                    break;
            }

        }
    }

    private void changeStop() {
        seekBar.setProgress(0);
    }

    private void changePlay() {
        if(musicService.mediaPlayer.isPlaying()){
            isPlay.setText("Play");
        }else{
            isPlay.setText("Paused");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks=am.getRunningAppProcesses();
        if(!tasks.isEmpty()){
            String topActivity=tasks.get(0).processName;
            if(!topActivity.equals(getPackageName())){
                return true;
            }
        }

        return false;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private void verifyStoragePermission(Activity activity) {
        int permission= ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }



    /*private void quit(){
        handler.removeCallbacks(runnable);
        unbindService(sc);
        try{
            finish();
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/



  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}


