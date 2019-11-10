package com.example.audios_player;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;
import org.w3c.dom.Entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView imageView_cd;
    Animation anim;
    TextView tv_content;
    String current_music_name = "只是不够爱";
    VideoView videoView;
    MediaController mediaController;
    TextView tv_music_name;
    LinkedHashMap<Integer, String> music_contents
            = new LinkedHashMap<Integer, String>(200,
            0.75f,
            true);
    ArrayList<String> music_list;
    String mode = "单曲循环";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        imageView_cd = findViewById(R.id.image_cd);
        imageView_cd.setImageResource(R.drawable.cd_image);

        tv_music_name = findViewById(R.id.music_name);
        tv_content = findViewById(R.id.music_content);
        videoView = (VideoView)findViewById(R.id.videoView);

        //init
        music_list = new ArrayList<String>();

        music_list.add(current_music_name);
        tv_music_name.setText(current_music_name);
        get_music_content();
        //加载指定的视频文件
        videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                + current_music_name +".mp4");

        //videoView.start();
        //创建MediaController对象
        mediaController = new MediaController(this);

        mediaController.setVisibility(View.VISIBLE);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        anim =new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(4000); // 设置动画时间
        //anim.setRepeatMode(AnimationSet.REVERSE);
        anim.setRepeatCount(-1);
        anim.setInterpolator(new LinearInterpolator()); // 设置插入器
        imageView_cd.startAnimation(anim);

        mediaController.setPrevNextListeners(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("state", "" + videoView.getCurrentPosition());
                        int i;
                        for (i=0;i<music_list.size();i++){
                            if(music_list.get(i).equals(current_music_name)){
                                break;
                            }
                        }
                        ++ i;
                        i %= music_list.size();
                        current_music_name = music_list.get(i);
                        tv_music_name.setText(current_music_name);
                        get_music_content();
                        //加载指定的视频文件
                        videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                                + current_music_name +".mp4");
                        videoView.start();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("state", "" + videoView.getDuration());
                        int i;
                        for (i=0;i<music_list.size();i++){
                            if(music_list.get(i).equals(current_music_name)){
                                break;
                            }
                        }
                        -- i;
                        if(i < 0)
                        {
                            i = music_list.size() - 1;
                        }
                        current_music_name = music_list.get(i);
                        tv_music_name.setText(current_music_name);
                        get_music_content();
                        //加载指定的视频文件
                        videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                                + current_music_name +".mp4");
                        videoView.start();
                    }
                });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mode.equals("单曲循环"))
                {
                    //current_music_name = music_list.get(position);
                    //tv_music_name.setText(current_music_name);
                    //get_music_content();
                    //加载指定的视频文件
                    videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                            + current_music_name +".mp4");
                    videoView.start();
                }
                else if(mode.equals("列表循环"))
                {
                    int i;
                    for (i=0;i<music_list.size();i++){
                        if(music_list.get(i).equals(current_music_name)){
                            break;
                        }
                    }
                    ++ i;
                    i %= music_list.size();
                    current_music_name = music_list.get(i);
                    //Log.d("name", current_music_name);
                    tv_music_name.setText(current_music_name);
                    get_music_content();
                    //加载指定的视频文件
                    videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                            + current_music_name +".mp4");
                    videoView.start();
                }
                else
                {
                    int min=0,max=music_list.size();
                    int rand = (int) (Math.random()*(max-min)+min);
                    current_music_name = music_list.get(rand);
                    tv_music_name.setText(current_music_name);
                    get_music_content();
                    //加载指定的视频文件
                    videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                            + current_music_name +".mp4");
                    videoView.start();
                }
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                tv_music_name.setText(current_music_name);
                get_music_content();
                //加载指定的视频文件
                videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                        + current_music_name +".mp4");
                videoView.start();
                return true;
            }
        });
        //让VideoView获取焦点
        videoView.requestFocus();

        class MyRunnable implements Runnable{
            private VideoView videoView;
            private String current_music_name;
            private TextView tv_content;
            private LinkedHashMap<Integer, String> music_contents;

            public MyRunnable() {

            }

            public MyRunnable(VideoView _videoView, String _current_music_name,
                              TextView _tv_content,
                              LinkedHashMap<Integer, String> _music_contents){
                this.videoView = _videoView;
                this.current_music_name = _current_music_name;
                this.tv_content = _tv_content;
                this.music_contents = _music_contents;
            }

            @Override
            public void run(){
                while (true)
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(videoView.isPlaying())
                    {
                        cd_control(true);

                        int time = videoView.getCurrentPosition();
                        //Log.d("time", time + "");
                        String content = "...";
                        for (Map.Entry<Integer, String> entry : music_contents.entrySet()) {
                            if(time < entry.getKey() - 200)
                            {
                                break;
                            }
                            else
                            {
                                content = entry.getValue();
                                //Log.d(" entry.getKey()",  entry.getKey() + "");
                            }
                        }
                        update_content(content, tv_content);
                    }
                    else
                    {
                        cd_control(false);
                    }
                }
            }
        }

        new Thread(new MyRunnable(videoView,
                current_music_name,
                tv_content,
                music_contents)).start();
    }

    private void update_content(final String content, final TextView tv_content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_content.setText(content);
            }
        });
    }

    public void cd_control(boolean flag)
    {
        if(flag)
        {
            anim.setDuration(4000); // 设置动画时间
        }
        else
        {
            try {
                anim.setDuration(4000000); // 设置动画时间
            }
            catch (Exception e)
            {

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void update_list(AlertDialog dialog_s)
    {
        listView = (ListView) dialog_s.findViewById(R.id.list);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,music_list);
        listView.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.music_list) {
            AlertDialog.Builder alterDiaglog
                    = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
            alterDiaglog.setView(R.layout.music_list_dialog);
            final AlertDialog dialog_s = alterDiaglog.create();
            dialog_s.show();

            update_list(dialog_s);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    current_music_name = music_list.get(position);
                    tv_music_name.setText(current_music_name);
                    get_music_content();
                    //加载指定的视频文件
                    videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                            + current_music_name +".mp4");
                    videoView.start();
                    dialog_s.dismiss();
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(music_list.size() == 1)
                    {
                        Toast tot = Toast.makeText(
                                MainActivity.this,
                                "删除失败",
                                Toast.LENGTH_LONG);
                        tot.show();
                        return false;
                    }
                    if(music_list.get(position).equals(current_music_name))
                    {
                        current_music_name = music_list.get((position + 1) % music_list.size());
                        tv_music_name.setText(current_music_name);
                        get_music_content();
                        //加载指定的视频文件
                        videoView.setVideoPath("http://www.bjybv5.site:128/static/uploads/music/"
                                + current_music_name +".mp4");
                    }
                    music_list.remove(position);
                    update_list(dialog_s);
                    Toast tot = Toast.makeText(
                            MainActivity.this,
                            "删除成功",
                            Toast.LENGTH_LONG);
                    tot.show();
                    return false;
                }
            });

            Button button_add = dialog_s.findViewById(R.id.add_music_button);
            button_add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alterDiaglog
                            = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
                    alterDiaglog.setView(R.layout.add_dialog);
                    final AlertDialog dialog = alterDiaglog.create();
                    dialog.show();

                    Button add = (Button) dialog.findViewById(R.id.edit_music);
                    //Log.d("button", String.valueOf(add));
                    final TextView tv_name = (TextView) dialog.findViewById(R.id.current_name);

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = String.valueOf(tv_name.getText());
                            if(name.equals("勇气")
                                    || name.equals("只是不够爱")
                                    || name.equals("独角戏"))
                            {
                                for(int i = 0; i < music_list.size(); ++ i)
                                {
                                    if(music_list.get(i).equals(name))
                                    {
                                        Toast tot = Toast.makeText(
                                                MainActivity.this,
                                                "添加失败",
                                                Toast.LENGTH_LONG);
                                        tot.show();
                                        dialog.dismiss();
                                        return;
                                    }
                                }
                                music_list.add(name);
                                Toast tot = Toast.makeText(
                                        MainActivity.this,
                                        "添加成功",
                                        Toast.LENGTH_LONG);
                                tot.show();
                                //update
                                update_list(dialog_s);
                            }
                            else
                            {
                                Toast tot = Toast.makeText(
                                        MainActivity.this,
                                        "添加失败",
                                        Toast.LENGTH_LONG);
                                tot.show();
                            }
                            dialog.dismiss();
                        }
                    });

                }
            });
        } else if (id == R.id.set) {
            if(mode.equals("单曲循环"))
            {
                mode = "列表循环";
            }
            else if(mode.equals("列表循环"))
            {
                mode = "随机播放";
            }
            else
            {
                mode = "单曲循环";
            }
            Toast tot = Toast.makeText(
                    MainActivity.this,
                    "当前模式为:" + mode,
                    Toast.LENGTH_LONG);
            tot.show();
        } else if (id == R.id.help) {
            Toast tot = Toast.makeText(
                    MainActivity.this,
                    "这是帮助",
                    Toast.LENGTH_LONG);
            tot.show();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void get_music_content()
    {
        class MyRunnable implements Runnable{
            LinkedHashMap<Integer, String> music_contents;
            private String current_music_name;

            public MyRunnable() {

            }

            public MyRunnable(LinkedHashMap<Integer, String> _music_contents,
                              String _current_music_name){
                this.music_contents = _music_contents;
                this.current_music_name = _current_music_name;
            }

            @Override
            public void run(){
                int HttpResult; // 服务器返回的状态
                String ee = new String();
                music_contents.clear();
                try
                {
                    URL url =new URL("http://www.bjybv5.site:128/static/uploads/contents/"
                            + current_music_name +".txt"); // 创建URL
                    URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
                    urlconn.connect();
                    HttpURLConnection httpconn =(HttpURLConnection)urlconn;
                    HttpResult = httpconn.getResponseCode();
                    if(HttpResult != HttpURLConnection.HTTP_OK) {
                        System.out.print("无法连接到");
                    } else {
                        int filesize = urlconn.getContentLength(); // 取数据长度
                        InputStreamReader isReader = new InputStreamReader(
                                urlconn.getInputStream(),"gb2312");
                        BufferedReader reader = new BufferedReader(isReader);
                        StringBuffer buffer = new StringBuffer();
                        String line; // 用来保存每行读取的内容
                        line = reader.readLine(); // 读取第一行
                        while (line != null) { // 如果 line 为空说明读完了
                            buffer.append(line); // 将读到的内容添加到 buffer 中
                            buffer.append(" "); // 添加换行符
                            String[] results = line.split(" ");
                            music_contents.put(Integer.parseInt(results[0]), results[1]);
                            line = reader.readLine(); // 读取下一行
                        }
                        System.out.print(buffer.toString());
                    }
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        new Thread(new MyRunnable(music_contents, current_music_name)).start();
    }
}
