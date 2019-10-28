package com.example.practice_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OtherActivity extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_other);
        setTitle("OtherActivity");

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        Integer age = intent.getIntExtra("age", 20);
        Toast.makeText(this,name + " " + age, Toast.LENGTH_LONG).show();

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                intent.putExtra("result","姓名:Lisi"+" 年龄"+"21");
                setResult(0,intent);

                finish();
            }
        });
    }
}
