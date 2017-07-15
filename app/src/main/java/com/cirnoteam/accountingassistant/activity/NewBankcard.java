package com.cirnoteam.accountingassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cirnoteam.com.cirnoteam.database.SaveDB;

import java.util.ArrayList;
import java.util.List;

import static com.cirnoteam.accountingassistant.R.id.amount_edit;
import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

import com.cirnoteam.accountingassistant.R;

public class NewBankcard extends AppCompatActivity {

    String type,account;

    private List<String> list_type = new ArrayList<String>();
    private Spinner spinner_type;
    private ArrayAdapter<String> adapter_type;

//    public void toBankcard(View view) {
//        Intent intent = new Intent(this, BankCard.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbankcard);

        list_type.add("支付宝");
        list_type.add("银行卡");
        list_type.add("现金");

        spinner_type = (Spinner)findViewById(R.id.spinner_type);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        spinner_type.setAdapter(adapter_type);

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

    }

    public void createAccount(View view){
        EditText editText_account = (EditText)findViewById(R.id.account_id);

        if(!TextUtils.isEmpty(editText_account.getText()))
            account = editText_account.getText().toString();
        else
            account = "0";

        //Toast.makeText(getApplicationContext(), amount+inout+account+type+time_edit+remark_edit, Toast.LENGTH_SHORT).show();
        if(SaveDB.saveAccount(this.getFilesDir().toString(),account,type))
            Toast.makeText(getApplicationContext(), "存储成功", Toast.LENGTH_SHORT).show();


        finish();
    }

}
