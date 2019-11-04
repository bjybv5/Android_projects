package com.example.calculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import static java.lang.Math.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String current_num = "0";
    private String variable = "x";
    private String function = "x";
    private TextView tv;

    long mLastTime=0;
    long mCurTime=0;

    private void show(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                current_num = result.toString();
                tv.setText(current_num);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);

        findViewById(R.id.button_c).setOnClickListener(this);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_sub).setOnClickListener(this);
        findViewById(R.id.button_mul).setOnClickListener(this);
        findViewById(R.id.button_div).setOnClickListener(this);
        findViewById(R.id.button_1).setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);
        findViewById(R.id.button_3).setOnClickListener(this);
        findViewById(R.id.button_4).setOnClickListener(this);
        findViewById(R.id.button_5).setOnClickListener(this);
        findViewById(R.id.button_6).setOnClickListener(this);
        findViewById(R.id.button_7).setOnClickListener(this);
        findViewById(R.id.button_8).setOnClickListener(this);
        findViewById(R.id.button_9).setOnClickListener(this);
        findViewById(R.id.button_0).setOnClickListener(this);
        findViewById(R.id.button_car).setOnClickListener(this);
        findViewById(R.id.button_sin).setOnClickListener(this);
        findViewById(R.id.button_cos).setOnClickListener(this);
        findViewById(R.id.button_fx).setOnClickListener(this);
        findViewById(R.id.button_dot).setOnClickListener(this);
        findViewById(R.id.button_equal).setOnClickListener(this);

        Button button_fx = findViewById(R.id.button_fx);
        button_fx.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
             public boolean onLongClick(View v) {
                AlertDialog.Builder alterDiaglog
                        = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
                alterDiaglog.setView(R.layout.dialog);
                final AlertDialog dialog = alterDiaglog.create();
                dialog.show();

                dialog.findViewById(R.id.set).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String variable_name = ((EditText)dialog.findViewById(R.id.variable_name))
                                .getText().toString();
                        String repres = ((EditText)dialog.findViewById(R.id.repres))
                                .getText().toString();

                        if(false)
                        {
                            Toast tot = Toast.makeText(
                                    MainActivity.this,
                                    "设置失败",
                                    Toast.LENGTH_LONG);
                            tot.show();
                        }
                        else
                        {
                            variable = variable_name;
                            function = repres;
                            Toast tot = Toast.makeText(
                                    MainActivity.this,
                                    "设置成功",
                                    Toast.LENGTH_LONG);
                            tot.show();
                        }
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });
        /*
        //监听button事件
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启线程，发送请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try {
                            String url_str = "http://www.bjybv5.site:64/evalute_representation";
                            url_str += "?representation=" + "2**10";
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
                            Log.d("log", url.toString());
                            //读取输入流
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder result = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            show(result.toString());
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
                }).start();

            }
        });
        */
    }

    public void evalute()
    {
        class MyRunnable implements Runnable{
            private String representation;

            public void MyRunnable(){

            }

            public MyRunnable(String _representation){
                this.representation=_representation;
            }

            @Override
            public void run(){
                representation = representation.replace("^", "**");
                representation = representation.replace("+", "%2B");

                //Log.d("representation", representation);
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String url_str = "http://www.bjybv5.site:64/evalute_representation";
                    url_str += "?representation=" + representation;
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
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    try{
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));

                        String r = jsonObject.optString("result", null);
                        //Log.d("r", r);
                        show(r);
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

        //开启线程，发送请求
        new Thread(new MyRunnable(current_num)).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_0:
                if(!current_num.equals("0"))
                {
                    current_num += "0";
                    tv.setText(current_num);
                }
                break;
            case R.id.button_1:
                if(current_num.equals("0"))
                {
                    current_num = "1";
                }
                else
                {
                    current_num += "1";
                }
                tv.setText(current_num);
                break;
            case R.id.button_2:
                if(current_num.equals("0"))
                {
                    current_num = "2";
                }
                else
                {
                    current_num += "2";
                }
                tv.setText(current_num);
                break;
            case R.id.button_3:
                if(current_num.equals("0"))
                {
                    current_num = "3";
                }
                else
                {
                    current_num += "3";
                }
                tv.setText(current_num);
                break;
            case R.id.button_4:
                if(current_num.equals("0"))
                {
                    current_num = "4";
                }
                else
                {
                    current_num += "4";
                }
                tv.setText(current_num);
                break;
            case R.id.button_5:
                if(current_num.equals("0"))
                {
                    current_num = "5";
                }
                else
                {
                    current_num += "5";
                }
                tv.setText(current_num);
                break;
            case R.id.button_6:
                if(current_num.equals("0"))
                {
                    current_num = "6";
                }
                else
                {
                    current_num += "6";
                }
                tv.setText(current_num);
                break;
            case R.id.button_7:
                if(current_num.equals("0"))
                {
                    current_num = "7";
                }
                else
                {
                    current_num += "7";
                }
                tv.setText(current_num);
                break;
            case R.id.button_8:
                if(current_num.equals("0"))
                {
                    current_num = "8";
                }
                else
                {
                    current_num += "8";
                }
                tv.setText(current_num);
                break;
            case R.id.button_9:
                if(current_num.equals("0"))
                {
                    current_num = "9";
                }
                else
                {
                    current_num += "9";
                }
                tv.setText(current_num);
                break;
            case R.id.button_dot:
                //if(current_num.indexOf(".") == -1)
                    current_num += ".";
                tv.setText(current_num);
                break;
            case R.id.button_c:
                current_num = "0";
                tv.setText(current_num);
                break;
            case R.id.button_back:
                current_num = current_num.substring(0, current_num.length() - 1);
                if(current_num.equals(""))
                {
                    current_num = "0";
                }
                tv.setText(current_num);
                break;
            case R.id.button_add:
                if(current_num.charAt(current_num.length() - 1) == '+'
                        || current_num.charAt(current_num.length() - 1) == '-'
                        || current_num.charAt(current_num.length() - 1) == '*'
                        || current_num.charAt(current_num.length() - 1) == '/'
                        || current_num.charAt(current_num.length() - 1) == '^')
                {
                    if(current_num.length() != 1)
                    {
                        current_num = current_num.substring(0, current_num.length() - 1);
                        current_num += "+";
                    }
                }
                else
                {
                    current_num += "+";
                }
                tv.setText(current_num);
                break;
            case R.id.button_sub:
                if(current_num.equals("0"))
                {
                    current_num = "-";
                }
                else if(current_num.charAt(current_num.length() - 1) == '+'
                        || current_num.charAt(current_num.length() - 1) == '-'
                        || current_num.charAt(current_num.length() - 1) == '*'
                        || current_num.charAt(current_num.length() - 1) == '/'
                        || current_num.charAt(current_num.length() - 1) == '^')
                {
                    current_num = current_num.substring(0, current_num.length() - 1);
                    current_num += "-";
                }
                else
                {
                    current_num += "-";
                }
                tv.setText(current_num);
                break;
            case R.id.button_mul:
                if(current_num.charAt(current_num.length() - 1) == '+'
                        || current_num.charAt(current_num.length() - 1) == '-'
                        || current_num.charAt(current_num.length() - 1) == '*'
                        || current_num.charAt(current_num.length() - 1) == '/'
                        || current_num.charAt(current_num.length() - 1) == '^')
                {
                    if(current_num.length() != 1)
                    {
                        current_num = current_num.substring(0, current_num.length() - 1);
                        current_num += "*";
                    }
                }
                else
                {
                    current_num += "*";
                }
                tv.setText(current_num);
                break;
            case R.id.button_div:
                if(current_num.charAt(current_num.length() - 1) == '+'
                        || current_num.charAt(current_num.length() - 1) == '-'
                        || current_num.charAt(current_num.length() - 1) == '*'
                        || current_num.charAt(current_num.length() - 1) == '/'
                        || current_num.charAt(current_num.length() - 1) == '^')
                {
                    if(current_num.length() != 1)
                    {
                        current_num = current_num.substring(0, current_num.length() - 1);
                        current_num += "/";
                    }
                }
                else
                {
                    current_num += "/";
                }
                tv.setText(current_num);
                break;
            case R.id.button_car:
                if(current_num.charAt(current_num.length() - 1) == '+'
                        || current_num.charAt(current_num.length() - 1) == '-'
                        || current_num.charAt(current_num.length() - 1) == '*'
                        || current_num.charAt(current_num.length() - 1) == '/'
                        || current_num.charAt(current_num.length() - 1) == '^')
                {
                    if(current_num.length() != 1)
                    {
                        current_num = current_num.substring(0, current_num.length() - 1);
                        current_num += "^";
                    }
                }
                else
                {
                    current_num += "^";
                }
                tv.setText(current_num);
                break;
            case R.id.button_sin:
                //evalute();
                try{
                    current_num = "" + sin(Double.parseDouble(current_num));
                    tv.setText(current_num);
                }
                catch (Exception exception)
                {

                }

                break;
            case R.id.button_cos:
                //evalute();
                try{
                    current_num = "" + cos(Double.parseDouble(current_num));
                    tv.setText(current_num);
                }
                catch (Exception exception)
                {

                }
                break;
            case R.id.button_equal:
                if(current_num.charAt(current_num.length() - 1) != '+'
                        && current_num.charAt(current_num.length() - 1) != '-'
                        && current_num.charAt(current_num.length() - 1) != '*'
                        && current_num.charAt(current_num.length() - 1) != '/'
                        && current_num.charAt(current_num.length() - 1) != '^')
                    evalute();
                break;
            case R.id.button_fx:
                current_num = function.replace(variable, current_num);
                evalute();
                break;
            default:
                break;
        }
    }
}
