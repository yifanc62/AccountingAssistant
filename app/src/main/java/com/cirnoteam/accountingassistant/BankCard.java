package com.cirnoteam.accountingassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cirnoteam.com.cirnoteam.database.ReadDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by tanzi on 2017/7/13.
 */

public class BankCard extends AppCompatActivity {
    private String[] bankcardnames = new String[]
            {"支付宝1","银行卡1","现金1"};

    private String[] bankcardnumbers = new String[]
            {"123", "456", "789",};

    public void toNewBankcard(View view) {
        Intent intent = new Intent(this, NewBankcard.class);
        startActivity(intent);
    }

//    public void toBankcard(View view) {
//        Intent intent = new Intent(this, BankCard.class);
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_card);
        //String[] bankcardnames = ReadDB.readAccount(this.getFilesDir().toString());
        //创建一个List集合，集合的元素是Map
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        for (int i = 0; i < bankcardnames.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("bankcardname", bankcardnames[i]);
            listItem.put("bankcardnumber", bankcardnumbers[i]);
            listItems.add(listItem);
        }

        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.simple_item,
                new String[]{"bankcardname", "bankcardnumber"},
                new int[]{R.id.bankcardname, R.id.bankcardnumber});
        ListView list = (ListView) findViewById(R.id.BankCardListView);
        //为ListView设置Adapter
        list.setAdapter(simpleAdapter);

        final AlertDialog delete = new AlertDialog.Builder(this).create();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        //删除list中的一行
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        delete.setTitle("删除数据");
        delete.setMessage("是否要删除这一行的数据？");
        delete.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);
        delete.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                listener);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete.show();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentToRecord = new Intent(BankCard.this, MainActivity.class);
                    startActivity(intentToRecord);
                    return true;
                case R.id.navigation_record:
                    Intent intentToUser = new Intent(BankCard.this, Record.class);
                    startActivity(intentToUser);
                    return true;
                case R.id.navigation_user:

                    return true;
            }
            return false;

        }

    };
}
