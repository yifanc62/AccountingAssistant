package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.Account;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pc on 2017/7/23.
 */

public class TransitPassword extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transit_password);
    }

    public void toChangePassword(View view){
        EditText editText1 = (EditText)findViewById(R.id.user_name);
        EditText editText2 = (EditText)findViewById(R.id.email);
        final String userName = editText1.getText().toString();
        final String email = editText2.getText().toString();

        new Thread() {
            public void run() {
                commitByPost(email,userName);
            }
        }.start();
    }

    public void commitByPost(String email,String userName) {

        try {
            UserUtils userUtils = new UserUtils(this);
            String deviceName = userUtils.getDefaultDeviceName();
            String uuid = userUtils.generateUuid();
            // 请求的地址
            String spec = "http://cirnoteam.varkarix.com/authreset";
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
                    + "&email=" + URLEncoder.encode(email, "UTF-8");
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
                String token = jsonObject.getJSONObject("entity").getString("resetToken");

                if(code == 200){
                    Intent intent = new Intent(this,ChangePassword.class);
                    intent.putExtra("resetToken",token);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里把返回的数据写在控件上 会出现什么情况尼
                            Toast.makeText(getApplicationContext(),"已向您的邮箱发送验证邮件",Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();;
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
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里把返回的数据写在控件上 会出现什么情况尼
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    }
                });

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
