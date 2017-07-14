package com.cirnoteam.com.cirnoteam.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Saika on 2017/7/14.
 */

public class UpdateDB extends AppCompatActivity {
    public static boolean updateRecord(String path, String expense, String amount, String remark, String type, String time, String id){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        db.beginTransaction();//开始事务
        try {
            db.execSQL("update RecordDetail " +
                            "set expense=?, amount=?, remark=?, type=?, time=? " +
                            "where recordid=?"
                    , new String[]{expense, amount, remark, type, time, id});
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
