package com.cirnoteam.accountingassistant.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Saika on 2017/7/14.
 */

public class ReadDB extends AppCompatActivity {
    public static String[] readRecord(String path) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        Cursor cursor = db.rawQuery("select * from RecordDetail", null);

        String[] str = new String[7];
        cursor.moveToLast();
        str[0] = cursor.getString(0);
        str[1] = cursor.getString(1);
        str[2] = cursor.getString(2);
        str[3] = cursor.getString(3);
        str[4] = cursor.getString(4);
        str[5] = cursor.getString(5);
        str[6] = cursor.getString(6);
        db.close();
        return str;
    }

    public static String[] readAccount(String path){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        Cursor cursor = db.rawQuery("select * from account", null);

        String[] str = new String[5];
        cursor.moveToLast();
        str[0] = cursor.getString(0);
        str[1] = cursor.getString(1);
        str[2] = cursor.getString(2);
        str[3] = cursor.getString(3);
        str[4] = cursor.getString(4);
        db.close();
        return str;
    }
}
