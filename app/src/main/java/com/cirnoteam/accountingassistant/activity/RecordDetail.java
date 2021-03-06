package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
//import com.cirnoteam.accountingassistant.database.ReadDB;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.entity.*;
import com.cirnoteam.accountingassistant.entity.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cirnoteam.accountingassistant.R.id.amount_edit;
import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/12.
 */

public class RecordDetail extends AppCompatActivity {

    //用于修改的临时变量
    String remark, recid;
    Long accountid;
    Date time;
    int type;
    float amount;
    boolean expense;
    boolean origionFlag = true;//设置类型初值的标记
    Long recordid;

    private List<String> list_inout = new ArrayList<String>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_account = new ArrayList<String>();
    private Spinner spinner_inout;
    private Spinner spinner_type;
    private Spinner spinner_account;
    private ArrayAdapter<String> adapter_inout;
    private ArrayAdapter<String> adapter_type;
    private ArrayAdapter<String> adapter_account;
    private TextView op;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail);
        initActionBar();

        //获取上一个Activity传过来的值(流水id)
        Intent intent = getIntent();
        recordid = intent.getLongExtra("recordid", -1L);

        list_inout.add("支出");
        list_inout.add("收入");
        AccountUtils u = new AccountUtils(this);
        List<Account> list_accounts = u.getAccountsOfBook(Status.bookid);
        List<Long> list_id = new ArrayList<>();
        for (int i = 0; i < list_accounts.size(); i++) {
            list_account.add(u.getDefaultAccountName(list_accounts.get(i).getType()) + " " + list_accounts.get(i).getName());
            list_id.add(list_accounts.get(i).getId());
        }
        final List<Long> list_idF = list_id;

        spinner_inout = (Spinner) findViewById(R.id.spinner_inout);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        op = (TextView) findViewById(R.id.textView_op);
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
            RecordUtils ut = new RecordUtils(this);
            com.cirnoteam.accountingassistant.entity.Record rec = ut.ReadRecordById(recordid);
            spinner_inout.setSelection(rec.getExpense() ? 0 : 1);
            editText_amount.setText(String.valueOf(rec.getAmount()));
            editText_remark.setText(rec.getRemark());
            //spinner_type.setSelection(rec.getType());
            type = rec.getType();
            editText_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rec.getTime()));
            for (int i = 0; i < list_accounts.size(); i++) {
                if (rec.getAccountid() == list_accounts.get(i).getId())
                    spinner_account.setSelection(i);
            }
            recid = String.valueOf(rec.getId());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "查询出错", Toast.LENGTH_SHORT).show();
        }
        //默认值设置完成

        /*******************监听事件***********************/
        spinner_inout.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (arg2 == 0)
                    op.setText(" -");
                else if (arg2 == 1)
                    op.setText(" +");
                expense = arg2 == 0;
                reSet_type(arg2);//重置类型spinner
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
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
        TableLayout T = (TableLayout) findViewById(R.id.tableLayout2);
        T.startAnimation(scaleAnimation);
    }

    //动态改变类型spinner
    private void reSet_type(int inout) {
        if (inout == 0) {//支出
            list_type.clear();
            list_type.add("一日三餐");
            list_type.add("购物消费");
            list_type.add("水电煤气");
            list_type.add("交通花费");
            list_type.add("医疗消费");
            list_type.add("其他支出");
            adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
            spinner_type.setAdapter(adapter_type);
            if (origionFlag) {
                spinner_type.setSelection(type);
                origionFlag = false;
            } else {
                spinner_type.setSelection(0);
            }
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
        } else if (inout == 1) {//收入
            list_type.clear();
            list_type.add("经营获利");
            list_type.add("工资收入");
            list_type.add("路上捡钱");
            list_type.add("其他收入");
            adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
            spinner_type.setAdapter(adapter_type);
            if (origionFlag) {
                spinner_type.setSelection(type - 6);
                origionFlag = false;
            } else {
                spinner_type.setSelection(0);
            }
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
        }
    }

    public void toModify(View view) {
        remark = ((EditText) findViewById(remark_edit)).getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean flag = false;
        try {
            time = df.parse(((EditText) findViewById(time_edit)).getText().toString());
            flag = true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "时间格式不对", Toast.LENGTH_SHORT).show();
        }
        if (flag) {
            EditText editText_amount = (EditText) findViewById(amount_edit);
            if (!TextUtils.isEmpty(editText_amount.getText()))
                amount = Float.parseFloat(editText_amount.getText().toString());
            else
                amount = 0;

            RecordUtils u = new RecordUtils(this);
            if (u.updateRecord(recordid, accountid, expense, amount, remark, type, time))
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void delete(View view) {
        RecordUtils u = new RecordUtils(this);
        if (u.deleteRecord(recordid)) {
            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_recorddetail_toolbar);
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



