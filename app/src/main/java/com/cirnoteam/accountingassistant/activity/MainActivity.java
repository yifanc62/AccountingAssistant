package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.BookUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.Book;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{;

    private List<String> bookName = new ArrayList<>();
    private List<String> record = new ArrayList<>();
    private List<Book> list_books = new ArrayList<>();
    private ImageButton leftmenu;
    private TextView mDate;
    private Spinner mySpinner;
    private ArrayAdapter<String> bookAdapter;
    private ListView myListView;
    private ArrayAdapter<String> recordAdapter;
    private List<com.cirnoteam.accountingassistant.entity.Record> records = new ArrayList<com.cirnoteam.accountingassistant.entity.Record>();
    //private Book book = new Book();
    private BookUtils bookUtils = new BookUtils(this);
    private UserUtils userUtils = new UserUtils(this);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_record:
                    Intent intentToRecord = new Intent(MainActivity.this, Record.class);
                    intentToRecord.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToRecord);
                    return true;
                case R.id.navigation_user:
                    Intent intentToUser = new Intent(MainActivity.this, BankCard.class);
                    intentToUser.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToUser);
                    return true;
            }
            return false;

        }

    };

    private void showInputDialog() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("请输入账本名称").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bookAdapter.insert(editText.getText().toString(),0);
                        if(bookUtils.addBook(userUtils.getCurrentUsername(),editText.getText().toString()))
                            Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        inputDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        Calendar cal = Calendar.getInstance();
        String currentDate = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH);
        mDate = (TextView) findViewById(R.id.Date);
        mDate.setText(currentDate);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftmenu = (ImageButton) findViewById(R.id.user);
        leftmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        mySpinner = (Spinner)findViewById(R.id.spinner_book);
//        第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list
        list_books = bookUtils.getAllBooks(userUtils.getCurrentUsername());
        for(Book list_book:list_books){
            bookName.add(list_book.getName());
        }
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookName);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(bookAdapter);
        //bookAdapter.add("默认账本");
        bookAdapter.add("＋");
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == (bookAdapter.getPosition("＋"))) {
                    showInputDialog();
                    arg0.setVisibility(View.VISIBLE);
                } else
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

                arg0.setVisibility(View.VISIBLE);
            }
        });
        //String newRecord = " ";
//        com.cirnoteam.accountingassistant.entity.Record newRecord = new com.cirnoteam.accountingassistant.entity.Record();
        RecordUtils recordUtils = new RecordUtils(this);
        records = recordUtils.queryBuilder();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        int i = 0;
        for(com.cirnoteam.accountingassistant.entity.Record _record : records) {
//            if(recordUtils.getRecordById((long)i).equals(null))
//                break;

            String newRecord = " ";
            newRecord += fm.format(_record.getTime());
            newRecord += " ";
            newRecord += _record.getExpense()?"收入 ":"支出 ";
            newRecord += _record.getAmount();
            newRecord += " ";
            switch (_record.getType()){
                case 0:newRecord += " 一日三餐";
                    break;
                case 1:newRecord += " 购物消费";
                    break;
                case 2:newRecord += " 水电煤气";
                    break;
                case 3:newRecord += " 交通花费";
                    break;
                case 4:newRecord += " 医疗消费";
                    break;
                case 5:newRecord += " 其他支出";
                    break;
                case 6:newRecord += " 经营获利";
                    break;
                case 7:newRecord += " 工资收入";
                    break;
                case 8:newRecord += " 路上捡钱";
                    break;
                case 9:newRecord += " 其他收入";
                    break;
            }


            record.add(newRecord);
            i++;
            if(i == 4)
                break;
        }
//        record.add("撒打算打算");
        myListView = (ListView) findViewById(R.id.listview);
        myListView.setDividerHeight(5);
        recordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, record);
        myListView.setAdapter(recordAdapter);


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Record.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toLogIn(View view) {
        Intent intent = new Intent(this, Record.class);
        startActivity(intent);
    }

    public void toNewRecord(View view) {
        Intent intent = new Intent(this, NewRecord.class);
        startActivity(intent);
    }

    public void toInquire(View view) {
        Intent intent = new Intent(this, Inquire.class);
        startActivity(intent);
    }
}
