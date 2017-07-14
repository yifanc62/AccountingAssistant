package com.cirnoteam.accountingassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cirnoteam.com.cirnoteam.database.ReadDB;
import com.cirnoteam.com.cirnoteam.database.UpdateDB;

import java.util.ArrayList;
import java.util.List;

import static com.cirnoteam.accountingassistant.R.id.amount_edit;
import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/12.
 */

public class RecordDetail extends AppCompatActivity {

    //用于修改的临时变量
    String type,account,remark,time,expense,recid,amount;

    private List<String> list_inout = new ArrayList<String>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_account = new ArrayList<String>();
    private Spinner spinner_inout;
    private Spinner spinner_type;
    private Spinner spinner_account;
    private ArrayAdapter<String> adapter_inout;
    private ArrayAdapter<String> adapter_type;
    private ArrayAdapter<String> adapter_account;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail);

        list_inout.add("支出");
        list_inout.add("收入");
        list_type.add("一日三餐");
        list_type.add("购物消费");
        list_type.add("水电煤气");
        list_type.add("交通花费");
        list_type.add("医疗消费");
        list_type.add("其他支出");
        list_type.add("经营获利");
        list_type.add("工资收入");
        list_type.add("路上捡钱");
        list_type.add("其他收入");
        while (true) {
            //TODO 查询数据库中的账户表，添加到list_account
            break;
        }


        spinner_inout = (Spinner) findViewById(R.id.spinner_inout);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        adapter_inout = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_inout);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        adapter_account = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_account);
        spinner_inout.setAdapter(adapter_inout);
        spinner_type.setAdapter(adapter_type);
        spinner_account.setAdapter(adapter_account);
        EditText editText_amount = (EditText) findViewById(amount_edit);
        EditText editText_remark = (EditText) findViewById(remark_edit);
        EditText editText_time = (EditText) findViewById(time_edit);
        //查询当前流水信息，填入各框(即设置默认值)
        /***
         * 示例
         editText.setText("10000");
         spinner_type.setSelection(3, true);
         spinner_inout.setSelection(1, true);
         spinner_account.setSelection(0, true);
         *setSelection即把该spinner的默认值设置为第一个参数所指的值
         ***/
        try {
            String[] str = ReadDB.readRecord(this.getFilesDir().toString());
            spinner_inout.setSelection(Integer.parseInt(str[1]));
            editText_amount.setText(str[2]);
            editText_remark.setText(str[3]);
            spinner_type.setSelection(Integer.parseInt(str[4]));
            editText_time.setText(str[6]);
            recid = str[0];
            Toast.makeText(getApplicationContext(), recid, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "表中无数据", Toast.LENGTH_SHORT).show();
        }
        //默认值设置完成

        /*******************监听事件***********************/
        spinner_inout.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                expense = String.valueOf(arg2);
                //Toast.makeText(getApplicationContext(), adapter_inout.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                type = String.valueOf(arg2);
                //Toast.makeText(getApplicationContext(), adapter_type.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spinner_account.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                account = adapter_account.getItem(arg2).toString();
                //Toast.makeText(getApplicationContext(), adapter_account.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }
    public void toModify(View view) {
        remark = ((EditText) findViewById(remark_edit)).getText().toString();
        time = ((EditText) findViewById(time_edit)).getText().toString();
        EditText editText_amount = (EditText) findViewById(amount_edit);
        if(!TextUtils.isEmpty(editText_amount.getText()))
            amount = editText_amount.getText().toString();
        else
            amount = "0";

        if(UpdateDB.updateRecord(this.getFilesDir().toString(),expense,amount,remark,type,time,recid))
            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();

        finish();
    }
    public void back(View view){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
}



