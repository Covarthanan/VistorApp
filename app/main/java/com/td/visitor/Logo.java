package com.td.visitor;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Logo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {


                File dir_user = new File(Environment.getExternalStorageDirectory(), "Visitor/User/");

                if (!dir_user.exists()) {

                    startActivity(new Intent(getApplicationContext(),Login_page.class));
                    finish();

                }else {

                    File file_user = new File(dir_user, "user.txt");
                    if (file_user.exists())
                    {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(file_user);
                            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

                            String user  = bufferedReader.readLine();

                            if (user.equals("empty"))
                            {
                                startActivity(new Intent(getApplicationContext(),Login_page.class));
                                finish();
                            }else
                            {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("user",user));
                                finish();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else
                    {
                        startActivity(new Intent(getApplicationContext(),Login_page.class));
                        finish();
                    }
                }
            }
        }.start();

    }
}
