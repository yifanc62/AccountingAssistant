package com.cirnoteam.accountingassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cirnoteam.accountingassistant.R;

public class NewBankcard extends AppCompatActivity {

    public void toBankcard(View view) {
//        Intent intent = new Intent(this, BankCard.class);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbankcard);
    }

}
