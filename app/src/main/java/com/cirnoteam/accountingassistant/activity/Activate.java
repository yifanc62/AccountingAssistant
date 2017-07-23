package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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
import java.security.MessageDigest;

import static com.cirnoteam.accountingassistant.activity.Register.convertByteToHexString;

public class Activate extends AppCompatActivity {

    private String token;
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("用户激活");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");


    }

    public void activate(View view)
    {
        EditText editText = (EditText)findViewById(R.id.edit_code);
        final String ActivateCode = editText.getText().toString();
        new Thread() {
            public void run() {
                ActivateByPost(token,ActivateCode,userName);
            }
        }.start();

    }

    public void ActivateByPost(String token, String ActivateCode,String userName) {

        try {
            UserUtils userUtils = new UserUtils(this);
            String deviceName = userUtils.getDefaultDeviceName();
            String uuid = userUtils.generateUuid();
            // 请求的地址
            String spec = "http://cirnoteam.varkarix.com/activate";
            // 根据地址创建URL对象
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
                    + "&token=" + URLEncoder.encode(token, "UTF-8")
                    + "&code=" + URLEncoder.encode(ActivateCode, "UTF-8")
                    + "&uuid=" + URLEncoder.encode(uuid,"UTF-8")
                    +" &device=" + URLEncoder.encode(deviceName,"UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                JSONObject jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("code");
                final String message = jsonObject.getString("message");

                if(code == 200){
                    userUtils.register(userName,password,token,uuid,deviceName);
                    Intent intent = new Intent(this,LogIn.class);

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里把返回的数据写在控件上 会出现什么情况尼
                            Toast.makeText(getApplicationContext(),"激活成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                    startActivity(intent);
                }

                else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里把返回的数据写在控件上 会出现什么情况尼
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // 通过runOnUiThread方法进行修改主线程的控件内容


            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里把返回的数据写在控件上 会出现什么情况尼
                        Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
