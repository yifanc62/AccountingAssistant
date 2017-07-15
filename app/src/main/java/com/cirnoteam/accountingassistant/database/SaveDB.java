package com.cirnoteam.accountingassistant.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Saika on 2017/7/14.
 */

public class SaveDB extends AppCompatActivity {
    public static boolean saveRecord(String path, String expense, String amount, String remark, String type, String time) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        db.beginTransaction();//开始事务
        try {
            //select max(recordid) from RecordDetail)+1
            db.execSQL("insert into RecordDetail values(null, ?, ?, ?, ?, null, ?, 0)"
                    , new String[]{expense, amount, remark, type, time});
        } catch (Exception e) {
            db.endTransaction();
            db.close();
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }
    public static boolean saveAccount(String path, String accountID,String type){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        db.beginTransaction();//开始事务
        try {
            //select max(recordid) from RecordDetail)+1
            db.execSQL("insert into account values(null, 1, ?, ?, null)"
                    , new String[]{accountID, type});
        } catch (Exception e){
            db.endTransaction();
            db.close();
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }
}


