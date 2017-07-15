package com.cirnoteam.accountingassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewBankcard extends AppCompatActivity {

    public void toBankcard(View view) {
        Intent intent = new Intent(this, BankCard.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbankcard);
    }

}
