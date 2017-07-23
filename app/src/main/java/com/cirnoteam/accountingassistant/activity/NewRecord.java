package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.entity.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cirnoteam.accountingassistant.R.id.amount_edit;
import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/13.
 */

public class NewRecord extends AppCompatActivity {
    //将要直接插入数据库的临时变量
    String remark;
    float amount;
    boolean expense;
    int type;
    Long accountid;
    Date time;

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
        setContentView(R.layout.new_record);
        initActionBar();

        list_inout.add("支出");
        list_inout.add("收入");
        AccountUtils u = new AccountUtils(this);
        List<Account> list_accounts = u.getAccountsOfBook(Status.bookid);
        List<Long> list_id = new ArrayList<>();
        for(int i=0;i<list_accounts.size();i++){
            list_account.add(u.getDefaultAccountName(list_accounts.get(i).getType()) + " " + list_accounts.get(i).getName());
            list_id.add(list_accounts.get(i).getId());
        }
        final List<Long> list_idF = list_id;


        spinner_inout = (Spinner) findViewById(R.id.spinner_inout);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        adapter_inout = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_inout);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        adapter_account = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_account);
        spinner_inout.setAdapter(adapter_inout);
        spinner_type.setAdapter(adapter_type);
        spinner_account.setAdapter(adapter_account);

        //设置默认值：时间
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        String str = dateFormater.format(curDate);
        EditText editText_time = (EditText) findViewById(time_edit);
        editText_time.setText(str);

        /*******************监听事件***********************/
        spinner_inout.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                expense = arg2==0;
                reSet_type(arg2);
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinner_account.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                accountid = list_idF.get(arg2);
                //Toast.makeText(getApplicationContext(), adapter_account.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });

        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        TableLayout T3 = (TableLayout) findViewById(R.id.T3);
        T3.startAnimation(scaleAnimation);
    }

    //动态改变类型spinner
    private void reSet_type(int inout){
        if(inout == 0) {
            list_type.clear();
            list_type.add("一日三餐");
            list_type.add("购物消费");
            list_type.add("水电煤气");
            list_type.add("交通花费");
            list_type.add("医疗消费");
            list_type.add("其他支出");
            adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
            spinner_type.setAdapter(adapter_type);
            spinner_type.setSelection(0);
            spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    type = arg2;
                    arg0.setVisibility(View.VISIBLE);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                    arg0.setVisibility(View.VISIBLE);
                }
            });
            //顺便改背景
            TableLayout T3 = (TableLayout) findViewById(R.id.T3);
            T3.setBackground(getResources().getDrawable(R.drawable.side_out));
        }else if(inout == 1){
            list_type.clear();
            list_type.add("经营获利");
            list_type.add("工资收入");
            list_type.add("路上捡钱");
            list_type.add("其他收入");
            adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
            spinner_type.setAdapter(adapter_type);
            spinner_type.setSelection(0);
            spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    type = arg2 + 6;
                    arg0.setVisibility(View.VISIBLE);
                }
                public void onNothingSelected(AdapterView<?> arg0) {
                    Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                    arg0.setVisibility(View.VISIBLE);
                }
            });
            //顺便改背景
            TableLayout T3 = (TableLayout) findViewById(R.id.T3);
            T3.setBackground(getResources().getDrawable(R.drawable.side_in));
        }
    }

    public void toCreate(View view) {
            EditText editText_amount = (EditText) findViewById(amount_edit);
            if (!TextUtils.isEmpty(editText_amount.getText()))
                amount = Float.parseFloat(editText_amount.getText().toString());
            else
                amount = 0;
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            boolean flag = false;
            try{
                time = fm.parse(((EditText) findViewById(time_edit)).getText().toString());
                flag = true;
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "时间格式不对", Toast.LENGTH_SHORT).show();
            }
            if(flag) {
                remark = ((EditText) findViewById(remark_edit)).getText().toString();
                RecordUtils u = new RecordUtils(this);
                if (u.addRecord(accountid, expense, amount, remark, type, time))
                    Toast.makeText(getApplicationContext(), "存储成功", Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_newrecord_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

