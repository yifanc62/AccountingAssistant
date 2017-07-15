package com.cirnoteam.accountingassistant.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/13.
 */

public class OpCtDatabase extends AppCompatActivity {
    public static boolean CreateTable(String path) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        if (db != null) {
            //流水记录
            String sql = "create table if not exists RecordDetail(" +
                    "recordid integer primary key AUTOINCREMENT," +
                    "expense integer(1) not null," +
                    "amount real not null," +
                    "remark text," +
                    "type integer not null," +
                    "remoteid integer," +
                    "time datetime not null," +
                    "accountid integer not null," +
                    "foreign key(accountid) references account(id))";
            db.execSQL(sql);

            //账户
            sql = "create table if not exists account(" +
                    "id integer primary key autoincrement," +
                    "bookid integer not null," +
                    "type integer not null," +
                    "name text," +
                    "remoteid integer," +
                    "foreign key(bookid) references book(id))";
            db.execSQL(sql);

            //账本
            sql = "create table if not exists book(" +
                    "id integer primary key autoincrement," +
                    "username text not null," +
                    "name text not null," +
                    "remoteid integer," +
                    "foreign key(username) references user(username))";
            db.execSQL(sql);

            //用户
            sql = "create table if not exists user(" +
                    "username text primary key," +
                    "password text not null," +
                    "token text not null," +
                    "devicename text not null," +
                    "uuid text not null," +
                    "avatar text," +
                    "current integer(1) not null," +
                    "lastsynctime timestamp not null)";
            db.execSQL(sql);

            //脏记录
            sql = "create table if not exists dirty(" +
                    "username text not null," +
                    "rid integer not null," +
                    "type integer default 0," +
                    "remoteid integer," +
                    "deleted integer(1))";
            //"time timestamp default current_timestamp on update current_timestamp)";
            db.execSQL(sql);
            db.close();
            return true;
        }
        return false;
    }
}
