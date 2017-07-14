package com.cirnoteam.com.cirnoteam.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/13.
 */

public class OpCtDatabase extends AppCompatActivity {
    public static boolean CreateTable(String path){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path + "temp.db3", null);
        if(db != null) {
            String sql = "create table if not exists RecordDetail(" +
                    "recordid integer primary key AUTOINCREMENT," +
                    //"accountid integer(5) primary key," +
                    "expense integer(1) not null," +
                    "amount real not null," +
                    "remark text," +
                    "type integer not null," +
                    "dirty integer not null," +
                    "time text not null)";
            //+"foreign key(accountid) references account(id)";
            db.execSQL(sql);
            db.close();
            return true;
        }
        return false;
    }
}
