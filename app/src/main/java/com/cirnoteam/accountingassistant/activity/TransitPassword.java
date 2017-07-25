package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pc on 2017/7/23.
 */

public class TransitPassword extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transit_password);
        initActionBar();
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.trpw_anim);
        tableLayout.startAnimation(scaleAnimation);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_transitpassword_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void toChangePassword(View view) {
        AlertDialog load = new AlertDialog.Builder(this).create();
        load.setMessage("数据处理中，请勿进行其他操作");
        load.show();
        String emailMatch = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailMatch);
        EditText editText1 = (EditText) findViewById(R.id.user_name);
        EditText editText2 = (EditText) findViewById(R.id.email);
        final String userName = editText1.getText().toString();
        final String email = editText2.getText().toString();
        Matcher matcher = pattern.matcher(email);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        AlertDialog empty = new AlertDialog.Builder(this).create();
        empty.setTitle("输入错误");
        empty.setMessage("输入框不能为空");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        AlertDialog emailError = new AlertDialog.Builder(this).create();
        emailError.setTitle("输入错误");
        emailError.setMessage("邮箱格式输入错误");
        emailError.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        if (userName.equals("") || email.equals(""))
            empty.show();
        else if (!matcher.matches())
            emailError.show();
        else {
            new Thread() {
                public void run() {
                    commitByPost(email, userName);
                }
            }.start();
        }
    }

    public void commitByPost(String email, String userName) {

        try {
            // 请求的地址
            String spec = "http://cirnoteam.varkarix.com/authreset";
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "username=" + URLEncoder.encode(userName, "UTF-8")
                    + "&email=" + URLEncoder.encode(email, "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                final String result = new String(baos.toByteArray());
                JSONObject jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("code");
                final String message = jsonObject.getString("message");


                if (code == 200) {
                    String token = jsonObject.getJSONObject("entity").getString("resetToken");
                    Intent intent = new Intent(this, ChangePassword.class);
                    intent.putExtra("resetToken", token);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "已向您的邮箱发送验证邮件", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                    ;
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // 通过runOnUiThread方法进行修改主线程的控件内容

            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
