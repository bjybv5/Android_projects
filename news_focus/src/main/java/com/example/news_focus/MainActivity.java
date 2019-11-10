package com.example.news_focus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private ImageView netImg;
    private PieChart pieChart;
    private BarChart barChart;
    private ListView listView;
    private List<String> news_list = null;
    private ArrayAdapter<String> adapter = null;
    private SearchView mSearchView;
    LinkedHashMap<String, String> title_link = null;
    LinkedHashMap<String, Integer> word_fre = null;

    public void get_news()
    {
        class MyRunnable implements Runnable{
            LinkedHashMap<String, String> title_link;

            public MyRunnable() {

            }

            public MyRunnable(LinkedHashMap<String, String> _title_link){
                this.title_link = _title_link;
            }

            @Override
            public void run(){
                int HttpResult; // 服务器返回的状态
                String ee = new String();
                title_link = new LinkedHashMap<String, String>();
                try
                {
                    URL url =new URL(
                            "http://www.bjybv5.site:128/static/uploads/title_link.txt");
                    //Log.d("url", String.valueOf(url));
                    URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
                    urlconn.connect();
                    HttpURLConnection httpconn =(HttpURLConnection)urlconn;
                    HttpResult = httpconn.getResponseCode();
                    if(HttpResult != HttpURLConnection.HTTP_OK) {
                        System.out.print("无法连接到");
                    } else {
                        int filesize = urlconn.getContentLength(); // 取数据长度
                        InputStreamReader isReader = new InputStreamReader(
                                urlconn.getInputStream(),"utf-8");
                        BufferedReader reader = new BufferedReader(isReader);
                        StringBuffer buffer = new StringBuffer();
                        String line; // 用来保存每行读取的内容
                        line = reader.readLine(); // 读取第一行
                        while (line != null) { // 如果 line 为空说明读完了
                            buffer.append(line); // 将读到的内容添加到 buffer 中
                            buffer.append(" "); // 添加换行符
                            String[] results = line.split(" ");
                            title_link.put(results[0], results[1]);
                            //Log.d("word", results[1]);
                            line = reader.readLine(); // 读取下一行
                        }
                    update_list(title_link);
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

        new Thread(new MyRunnable(title_link)).start();
    }

    private void update_list(final LinkedHashMap<String, String> _title_link)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title_link = _title_link;
                update_list("");
            }
        });
    }

    private void update_list(String pattern)
    {
        news_list = new ArrayList<String>();

        if(pattern.equals(""))
        {
            for (Map.Entry<String, String> entry : title_link.entrySet()) {
                news_list.add(entry.getKey());
            }
        }
        else
        {
            for (Map.Entry<String, String> entry : title_link.entrySet()) {
                if(entry.getKey().contains(pattern))
                {
                    news_list.add(entry.getKey());
                }
            }
        }

        adapter =new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,news_list);
        listView.setAdapter(adapter);
    }

    private void show_word_cloud()
    {
        Glide.with(this).load(
                "http://www.bjybv5.site:128/static/uploads/cloud.jpg")
                .into(netImg);
    }

    public void get_word_fre()
    {
        class MyRunnable implements Runnable{
            LinkedHashMap<String, Integer> word_fre;
            PieChart pieChart;
            BarChart barChart;

            public MyRunnable() {

            }

            public MyRunnable(LinkedHashMap<String, Integer> _word_fre,
                              BarChart _barChart,
                              PieChart _pieChart){
                this.word_fre = _word_fre;
                this.barChart = _barChart;
                this.pieChart = _pieChart;
            }

            @Override
            public void run(){
                int HttpResult; // 服务器返回的状态
                String ee = new String();
                word_fre = new LinkedHashMap<String, Integer>();
                Log.d("", "");
                try
                {
                    URL url =new URL(
                            "http://www.bjybv5.site:128/static/uploads/word_fre.txt");
                    //Log.d("url", String.valueOf(url));
                    URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
                    urlconn.connect();
                    HttpURLConnection httpconn =(HttpURLConnection)urlconn;
                    HttpResult = httpconn.getResponseCode();
                    if(HttpResult != HttpURLConnection.HTTP_OK) {
                        System.out.print("无法连接到");
                    } else {
                        int filesize = urlconn.getContentLength(); // 取数据长度
                        InputStreamReader isReader = new InputStreamReader(
                                urlconn.getInputStream(),"utf-8");
                        BufferedReader reader = new BufferedReader(isReader);
                        StringBuffer buffer = new StringBuffer();
                        String line; // 用来保存每行读取的内容
                        line = reader.readLine(); // 读取第一行
                        while (line != null) { // 如果 line 为空说明读完了
                            buffer.append(line); // 将读到的内容添加到 buffer 中
                            buffer.append(" "); // 添加换行符
                            String[] results = line.split(" ");
                            word_fre.put(results[0], Integer.parseInt(results[1]));
                            //Log.d("word", results[0]);
                            line = reader.readLine(); // 读取下一行
                        }
                        showChart(word_fre, barChart, pieChart);
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

        new Thread(new MyRunnable(word_fre, barChart, pieChart)).start();
    }

    private void showChart(PieChart pieChart, PieData pieData) {
        // 设置描述，我设置了不显示，因为不好看，你也可以试试让它显示，真的不好看
        Description description = new Description();
        description.setEnabled(true);
        description.setText("");
        pieChart.setDescription(description);
        //pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(60f); //半径
        pieChart.setTransparentCircleRadius(64f); // 半透明圈
        //pieChart.setHoleRadius(0) //实心圆
        //pieChart.setDescription("测试饼状图");
        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true); //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度
        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转
        // display percentage values
        pieChart.setUsePercentValues(false); //显示成百分比
        //pieChart.setCenterTextRadiusPercent(80f);
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);
        // add a selection listener
        // mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);
        // mChart.setOnAnimationListener(this);
        pieChart.setCenterText(""); //饼状图中间的文字
        //设置数据
        pieChart.setData(pieData);
        // undo all highlights
        // pieChart.highlightValues(null);
        // pieChart.invalidate();
        Legend mLegend = pieChart.getLegend(); //设置比例图
        //mLegend.setPosition(LegendPosition.RIGHT_OF_CHART); //最右边显示
        // mLegend.setForm(LegendForm.LINE); //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        pieChart.animateXY(1000, 1000); //设置动画
        // mChart.spin(2000, 0, 360);
    }

    private PieData getPieData(LinkedHashMap<String, Integer> word_fre) {
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        float sum = 0;
        ArrayList<Float> value_percent = new ArrayList<Float>();

        for (Map.Entry<String, Integer> entry : word_fre.entrySet()) {
            value_percent.add((float) (.0 + entry.getValue()));
            sum += (float) (.0 + entry.getValue());
        }

        for(int i = 0; i < value_percent.size(); ++ i)
        {
            yValues.add(new PieEntry(value_percent.get(i) / sum, i));
            //yValues.add(new PieEntry(value_percent.get(i), i));
        }

        //y轴的集合
        List<PieEntry> strings = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : word_fre.entrySet()) {
            strings.add(new PieEntry(entry.getValue() / sum, entry.getKey()));
        }
        PieDataSet pieDataSet = new PieDataSet(strings,
                "关键词"/*显示在比例图上*/);
        //pieDataSet.setValueTextColor(0);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        PieData pieData = new PieData(pieDataSet);
        return pieData;
    }

    private void showChart(final LinkedHashMap<String, Integer> word_fre,
                           final BarChart barChart, final PieChart pieChart) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barChart.setDrawBarShadow(false);//true绘画的Bar有阴影。
                barChart.setDrawValueAboveBar(true);//true文字绘画在bar上
                barChart.getDescription().setEnabled(false);
                barChart.setMaxVisibleValueCount(60);
                barChart.setPinchZoom(false);//false只能单轴缩放
                barChart.setDrawGridBackground(false);
                //x坐标轴设置
                ArrayList<String> xAxisValue = new ArrayList<String>();
                for (Map.Entry<String, Integer> entry : word_fre.entrySet()) {
                    xAxisValue.add(entry.getKey());
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(true);
                xAxis.setGranularity(1);
                xAxis.setLabelCount(xAxisValue.size());
                xAxis.setCenterAxisLabels(true);//设置标签居中
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValue));

                int count = 10;

                int start = 1;
                int max = -1;
                ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                for (int i = start; i < start + count; i++) {
                    int val = word_fre.get(xAxisValue.get(i - 1));
                    yVals1.add(new BarEntry(i, val));
                    max = max > val ? max : val;
                }

                //设置Y轴
                barChart.getAxisRight().setEnabled(false);
                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setLabelCount(10, false);
//        leftAxis.setValueFormatter();
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                leftAxis.setSpaceTop(10);
                leftAxis.setAxisMinimum(0);
                leftAxis.setAxisMaximum(max + 1);

                Legend l = barChart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);
                l.setForm(Legend.LegendForm.SQUARE);
                l.setFormSize(9);
                l.setTextSize(11);
                l.setXEntrySpace(4);

                BarDataSet set1 = new BarDataSet(yVals1, "关键词");
                set1.setDrawIcons(false);
                set1.setColor(ColorTemplate.rgb("#2ecc71"));

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);
                BarData data = new BarData(dataSets);
                data.setValueTextSize(10);
                barChart.setData(data);

                PieData mPieData = getPieData(word_fre);
                showChart(pieChart, mPieData);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    netImg.setVisibility(INVISIBLE);
                    barChart.setVisibility(INVISIBLE);
                    pieChart.setVisibility(INVISIBLE);

                    listView.setVisibility(VISIBLE);
                    mSearchView.setVisibility(VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    listView.setVisibility(INVISIBLE);
                    mSearchView.setVisibility(INVISIBLE);
                    barChart.setVisibility(INVISIBLE);
                    pieChart.setVisibility(INVISIBLE);

                    netImg.setVisibility(VISIBLE);
                    show_word_cloud();
                    return true;
                case R.id.navigation_notifications:
                    listView.setVisibility(INVISIBLE);
                    mSearchView.setVisibility(INVISIBLE);
                    netImg.setVisibility(INVISIBLE);

                    barChart.setVisibility(VISIBLE);
                    pieChart.setVisibility(VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        listView = (ListView) findViewById(R.id.news_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = title_link.get(news_list.get(position));
                Intent intent = new Intent(MainActivity.this,
                        WebViewActivity.class);

                intent.putExtra("url", url);
                startActivityForResult(intent,0);
            }
        });
        this.registerForContextMenu(listView);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String pattern) {
                update_list(pattern);
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

        barChart = (BarChart) findViewById(R.id.bar_chart);
        pieChart = (PieChart) findViewById(R.id.pie_chart);
        netImg = findViewById(R.id.img);

        netImg.setVisibility(INVISIBLE);
        barChart.setVisibility(INVISIBLE);
        pieChart.setVisibility(INVISIBLE);

        listView.setVisibility(VISIBLE);
        mSearchView.setVisibility(VISIBLE);

        get_news();
        get_word_fre();
    }
}
