package com.example.roman.tcpclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    EditText etIp ;
    EditText etPort ;
    EditText etExpression ;
    EditText etLogPath ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etIp = (EditText)findViewById(R.id.etIp);
        etPort = (EditText)findViewById(R.id.etPort);
        etExpression = (EditText)findViewById(R.id.etExpression);
        etLogPath = (EditText)findViewById(R.id.etPath);
        Button button = findViewById(R.id.button);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(new File("/storage/emulated/0/settings.txt"));
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            TcpClient.HOST = new String (bytes);
            etIp.setText(TcpClient.HOST);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expression;
                if (etLogPath.getText().toString().compareTo("") != 0) {
                    TcpClient.LOG_PATH = etLogPath.getText().toString();
                } else {
                    TcpClient.LOG_PATH = "test.txt";
                }

                if(etIp.getText().toString().compareTo("") != 0){
                    TcpClient.HOST = etIp.getText().toString();
                }

                if(etPort.getText().toString().compareTo("") != 0){
                    String text = etPort.getText().toString();
                    TcpClient.PORT = Integer.parseInt(etPort.getText().toString());
                }

                if(etExpression.getText().toString().compareTo("") != 0){
                    expression = etExpression.getText().toString();
                } else {
                    expression = "1+1";
                }
                TcpClient client = new TcpClient(expression, getApplicationContext(), mSettings);
                etIp.setText(TcpClient.HOST);
                Thread thread = new Thread(client);
                thread.start();
                Toast.makeText(getBaseContext(), "Результат получен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
