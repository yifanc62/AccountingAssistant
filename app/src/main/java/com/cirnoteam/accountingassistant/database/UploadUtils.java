package com.cirnoteam.accountingassistant.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Black Lotus on 2017/7/24.
 */

public class UploadUtils {

    /**
     * @param username
     * @param file
     * @throws IOException
     */
    public static void post(String username, File file)
            throws IOException {

        String spec = "http://cirnoteam.varkarix.com/avatar";
        URL url = new URL(spec);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("Content-Type", "multipart/form-data");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String CHARSET = "UTF-8";
        // 首先组拼文本类型的参数
        String text = PREFIX +
                BOUNDARY +
                LINEND +
                "Content-Disposition: form-data; name=\"username\"" + LINEND +
                "Content-Type: text/plain; charset=" + CHARSET + LINEND +
                "Content-Transfer-Encoding: 8bit" + LINEND +
                LINEND +
                username +
                LINEND;

        OutputStream os = urlConnection.getOutputStream();
        os.write(text.getBytes());
        // 发送文件数据
        if (file != null) {
            String fileHeader = PREFIX +
                    BOUNDARY +
                    LINEND +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + username + "\"" + LINEND +
                    "Content-Type: application/octet-stream; charset=" + CHARSET + LINEND +
                    LINEND;
            os.write(fileHeader.getBytes());
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.write(LINEND.getBytes());
        }
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        os.write(end_data);
        os.flush();
        // 得到响应码
        int res = urlConnection.getResponseCode();
        InputStream in = urlConnection.getInputStream();
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
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
            try {
                JSONObject object = new JSONObject(result);
                if(object.getInt("code")!=200){
                    throw new IOException(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (res == 400) {

        } else {

        }
        os.close();
        urlConnection.disconnect();
    }


    public static Bitmap getImage(String username) {
        try {
            String url = "http://cirnoteam.varkarix.com/avatar";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestMethod("Content-Type:image/png;");
            System.out.println("tdw1");
            OutputStream os = conn.getOutputStream();
            String content = "username="+username;
            os.write(content.getBytes());
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
