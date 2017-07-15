package com.cirnoteam.com.cirnoteam.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Saika on 2017/7/15.
 */

public class DeleteDB extends AppCompatActivity {
    public static boolean deleteRecord(String path, String id, String username){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        db.beginTransaction();//开始事务
        try {
            db.execSQL("delete from RecordDetail " +
                            "where recordid = id",
                             new String[]{id});
            db.execSQL("insert into dirty values(?, ?, 1, null, 1)", new String[]{username, id});
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
