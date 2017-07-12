package com.cirnoteam.accountingassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Register Class
 * @author UZ
 * @version 1.0
 */
public class Register extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

    }

    public void register(View view) {
        String account = "";
        String password_1 = "";
        String password_2 = "";
        String email = "";

        EditText editText1 = (EditText) findViewById(R.id.account);
        account = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.password_1);
        password_1 = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.password_2);
        password_2 = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.email);
        email = editText4.getText().toString();

        //TODO：添加新用户至数据库
    }
}
