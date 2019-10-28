package com.example.practice_5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private int time = 0;
    private boolean thread_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.txtContent);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == -1)
                {
                    textView.setText("Complete");
                }
                else
                {
                    textView.setText(msg.arg1+"");
                }
            }
        };

        final Runnable myWorker = new Runnable() {
            @Override
            public void run() {
                while(thread_flag){
                    //Log.d("tip", String.valueOf(thread_flag));
                    Message msg = new Message();
                    msg.arg1 = time;
                    handler.sendMessage(msg);
                    time += 1;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        final Thread workThread = new Thread(null, myWorker, "WorkThread");
        workThread.start();

        Button button = (Button) findViewById(R.id.btnStart);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread_flag = !thread_flag;
                //Log.d("tip", String.valueOf(thread_flag));
                workThread.start();
            }
        });
    }
}
