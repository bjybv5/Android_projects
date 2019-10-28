package com.example.practice_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alterDiaglog
                        = new AlertDialog.Builder(MainActivity.this, R.style.MyDialog);
                alterDiaglog.setView(R.layout.dialog);
                final AlertDialog dialog = alterDiaglog.create();
                dialog.show();

                dialog.findViewById(R.id.login).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String user_id = ((EditText)dialog.findViewById(R.id.userId_input))
                                .getText().toString();
                        String password = ((EditText)dialog.findViewById(R.id.password_input))
                                .getText().toString();

                        Log.d("id", user_id);
                        Log.d("password", password);
                        if(user_id.equals("abc") && password.equals("123"))
                        {
                            Toast tot = Toast.makeText(
                                    MainActivity.this,
                                    "成功",
                                    Toast.LENGTH_LONG);
                            tot.show();
                        }
                        else
                        {
                            Toast tot = Toast.makeText(
                                    MainActivity.this,
                                    "错误",
                                    Toast.LENGTH_LONG);
                            tot.show();
                        }
                    }
                });
            }
        });
    }
}
