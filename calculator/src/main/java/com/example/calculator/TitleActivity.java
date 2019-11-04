package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.INVISIBLE;

public class TitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        //VideoView videoView = (VideoView)findViewById(R.id.videoView);

        //Log.d("log", Environment.getExternalStorageDirectory().getPath());
        //加载指定的视频文件
        /*String path = Environment.getExternalStorageDirectory().getPath()+"/test_video.mp4";
        videoView.setVideoPath(path);
        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();*/

        final ImageView iv;

        iv = (ImageView)findViewById(R.id.imageView);
        iv.setImageResource(R.drawable.cloud_text);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 1)
                {
                    Intent intent = new Intent(TitleActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        final Runnable myWorker = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        };

        Thread start_title_worker = new Thread(null, myWorker, "WorkThread");
        start_title_worker.start();
    }
}
