package com.cirnoteam.accountingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
