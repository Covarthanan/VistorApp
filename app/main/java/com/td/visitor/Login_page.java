package com.td.visitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_page extends AppCompatActivity {

    EditText username;
    EditText password;
    Button login_btn;
    String user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login_btn = (Button)findViewById(R.id.login_btn);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username_str = username.getText().toString();
                String password_str = password.getText().toString();

                if (username_str.equals("Chairman") || username_str.equals("Deputy Chairman") || username_str.equals("Reception"))
                {
                    if (password_str.equals("1234"))
                    {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("user",username_str));
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid password..:(",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Invalid user..:(",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
