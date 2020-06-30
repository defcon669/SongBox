package com.dominator.songbox;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView albumArt;
    private TextView lTime;
    private TextView rTime;
    private Button prevBtn;
    private Button playBtn;
    private Button nextBtn;
    private SeekBar mseekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lTime = (TextView) findViewById(R.id.lTime);
        rTime = (TextView) findViewById(R.id.rTime);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        playBtn = (Button) findViewById(R.id.playBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        mseekBar = (SeekBar) findViewById(R.id.mseekBar);

        final MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.nickelbackrock);

        mseekBar.setMax(mediaPlayer.getDuration());
        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                lTime.setText(simpleDateFormat.format(new Date(currentPos)));
                rTime.setText(simpleDateFormat.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
                } else if (mediaPlayer != null) {
                    mediaPlayer.start();
                    updateThread();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
                }
            }

            private void updateThread() {
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        try {
                            while (mediaPlayer != null && mediaPlayer.isPlaying()) {

                                Thread.sleep(50);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int newPosition = mediaPlayer.getCurrentPosition();
                                        int newMax = mediaPlayer.getDuration();
                                        mseekBar.setMax(newMax);
                                        mseekBar.setProgress(newPosition);

                                        lTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                                .format(new Date(mediaPlayer.getCurrentPosition()))));

                                        rTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                                .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));

                                    }
                                });
                            }
                            super.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }; thread.start();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(0);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}