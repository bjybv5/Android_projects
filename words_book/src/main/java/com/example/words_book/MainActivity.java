package com.example.words_book;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    MyDatabaseHelper dbHelper;
    private ListView listView;
    private List<String> list = null;
    private ArrayAdapter<String> adapter = null;

    private void speak(String word)
    {
        MediaPlayer mp=new MediaPlayer();
        Uri uri=Uri.parse("http://www.bjybv5.site:128/static/uploads/audios/" +
                word + ".mp3");
        try {
            mp.setDataSource(this,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    private void insert(final String word, final String tanslate_word, final String example) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put("word", word);
                values.put("translate_word", tanslate_word);
                values.put("example", example);
                db.insert("words", null, values);

                speak(word);
                update_list();
            }
        });
    }

    private void edit(final String original_word, final String current_word,
                      final String tanslate_word, final String example) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "update WORDS set word = '" + current_word +
                        "' where word = '" + original_word + "'";
                db.execSQL(sql);

                sql = "update WORDS set translate_word = '" + tanslate_word +
                        "' where word = '" + current_word + "'";
                db.execSQL(sql);

                sql = "update WORDS set example = '" + example +
                        "' where word = '" + current_word + "'";
                db.execSQL(sql);

                update_list();
            }
        });
    }

    private void update_list()
    {
        list = new ArrayList<String>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from WORDS";
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                //Log.d("log", cursor.getString(cursor.getColumnIndex("word")));
                list.add(cursor.getString(cursor.getColumnIndex("word")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        adapter =new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
    }

    private void select(String pattern)
    {
        list = new ArrayList<String>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from WORDS where word like '%" + pattern + "%'";
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst())
        {
            do{
                //Log.d("log", cursor.getString(cursor.getColumnIndex("word")));
                list.add(cursor.getString(cursor.getColumnIndex("word")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        adapter =new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
    }

    private void show_word_detail(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from WORDS where word = '" + list.get(id) + "'";
        Cursor cursor = db.rawQuery(sql, null);

        String word = null;
        String translate_word = null;
        String example = null;

        if(cursor.moveToFirst())
        {
            do{
                word = cursor.getString(cursor.getColumnIndex("word"));
                translate_word = cursor.getString(cursor.getColumnIndex("translate_word"));
                example = cursor.getString(cursor.getColumnIndex("example"));
            }while (cursor.moveToNext());
        }
        cursor.close();

        AlertDialog.Builder alterDiaglog_detail
                = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
        alterDiaglog_detail.setView(R.layout.word_detail_dialog);
        final AlertDialog dialog_detail = alterDiaglog_detail.create();
        dialog_detail.show();

        TextView word_tv = (TextView)dialog_detail.findViewById(R.id.word);
        TextView translate_word_tv = (TextView)dialog_detail.findViewById(R.id.translate_word);
        TextView example_tv = (TextView)dialog_detail.findViewById(R.id.example);
        ImageButton ib = (ImageButton)dialog_detail.findViewById(R.id.imageButton);

        word_tv.setText(word);
        translate_word_tv.setText(translate_word);
        example_tv.setText(example);
        final String finalWord = word;
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(finalWord);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        //设置Menu显示内容
        menu.add(1,1,1,"修改");
        menu.add(1,2,1,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        SQLiteDatabase db = null;
        String sql = null;
        switch(item.getItemId()){
            case 1:
                AlertDialog.Builder alterDiaglog_edit
                        = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
                alterDiaglog_edit.setView(R.layout.edit_dialog);
                final AlertDialog dialog_edit = alterDiaglog_edit.create();
                dialog_edit.show();

                dialog_edit.findViewById(R.id.edit_word).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        class MyRunnable implements Runnable{
                            private String original_word;
                            private String current_word;

                            public MyRunnable() {

                            }

                            public MyRunnable(String _original_word,
                                              String _current_word){
                                this.original_word = _original_word;
                                this.current_word = _current_word;
                            }

                            @Override
                            public void run(){
                                HttpURLConnection connection = null;
                                BufferedReader reader = null;
                                try {
                                    String url_str = "http://www.bjybv5.site:64/translate";
                                    url_str += "?word=" + current_word;
                                    URL url = new URL(url_str);

                                    connection = (HttpURLConnection) url.openConnection();
                                    //设置请求方法
                                    connection.setRequestMethod("GET");
                                    //设置连接超时时间（毫秒）
                                    //connection.setConnectTimeout(5000);
                                    //设置读取超时时间（毫秒）
                                    //connection.setReadTimeout(5000);

                                    //返回输入流
                                    InputStream in = connection.getInputStream();
                                    //Log.d("log", url.toString());
                                    //读取输入流
                                    reader = new BufferedReader(new InputStreamReader(in));
                                    StringBuilder result = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        result.append(line);
                                    }
                                    try{
                                        JSONObject jsonObject = new JSONObject(String.valueOf(result));

                                        String translate_word = jsonObject.optString("translate_word", null);
                                        String example = jsonObject.optString("example", null);
                                        //Log.d("r", r);
                                        edit(original_word, current_word, translate_word, example);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (ProtocolException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (reader != null) {
                                        try {
                                            reader.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (connection != null) {//关闭连接
                                        connection.disconnect();
                                    }
                                }
                            }
                        }

                        String current_word = ((EditText) dialog_edit.findViewById(R.id.current_word))
                                .getText().toString();
                        //开启线程，发送请求
                        new Thread(new MyRunnable(list.get(menuInfo.position), current_word)).start();
                        dialog_edit.dismiss();
                    }
                });
                break;
            case 2:
                db = dbHelper.getWritableDatabase();
                sql = "delete from WORDS where word = '" + list.get(menuInfo.position) + "'";
                db.execSQL(sql);
                update_list();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int or = getResources().getConfiguration().orientation;
        if(or == Configuration.ORIENTATION_LANDSCAPE)
        {
            setContentView(R.layout.main_land);
        }
        else if(or == Configuration.ORIENTATION_PORTRAIT)
        {
            setContentView(R.layout.activity_main);
        }

        dbHelper = new MyDatabaseHelper(this, "words_book.db",
                null, 1);

        listView = (ListView) findViewById(R.id.list_view);

        update_list();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_word_detail(position);
            }
        });
        this.registerForContextMenu(listView);

        SearchView mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String pattern) {
                //Log.d("text", pattern);
                select(pattern);
                return false;
            }
        });

        Button button_help = findViewById(R.id.button_help);
        button_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tot = Toast.makeText(
                        MainActivity.this,
                        "这是帮助",
                        Toast.LENGTH_LONG);
                tot.show();
            }
        });

        Button button_add = findViewById(R.id.button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class MyRunnable implements Runnable{
                    private String word;

                    public MyRunnable() {

                    }

                    public MyRunnable(String _word){
                        this.word=_word;
                    }

                    @Override
                    public void run(){
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try {
                            String url_str = "http://www.bjybv5.site:64/translate";
                            url_str += "?word=" + word;
                            URL url = new URL(url_str);

                            connection = (HttpURLConnection) url.openConnection();
                            //设置请求方法
                            connection.setRequestMethod("GET");
                            //设置连接超时时间（毫秒）
                            //connection.setConnectTimeout(5000);
                            //设置读取超时时间（毫秒）
                            //connection.setReadTimeout(5000);

                            //返回输入流
                            InputStream in = connection.getInputStream();
                            //Log.d("log", url.toString());
                            //读取输入流
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder result = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            try{
                                JSONObject jsonObject = new JSONObject(String.valueOf(result));

                                String translate_word = jsonObject.optString("translate_word", null);
                                String example = jsonObject.optString("example", null);
                                //Log.d("r", r);
                                insert(word, translate_word, example);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (connection != null) {//关闭连接
                                connection.disconnect();
                            }
                        }
                    }
                }

                AlertDialog.Builder alterDiaglog_insert
                        = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
                alterDiaglog_insert.setView(R.layout.add_dialog);
                final AlertDialog dialog_s = alterDiaglog_insert.create();
                dialog_s.show();

                dialog_s.findViewById(R.id.edit_word).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String current_word = ((EditText)dialog_s.findViewById(R.id.current_word))
                                .getText().toString();
                        //开启线程，发送请求
                        new Thread(new MyRunnable(current_word)).start();
                        dialog_s.dismiss();
                    }
                });
            }
        });
    }
}
