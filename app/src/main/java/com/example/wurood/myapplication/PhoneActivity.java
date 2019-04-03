package com.example.wurood.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneActivity extends AppCompatActivity {
    @BindView(R.id.spinnerCountries)
    Spinner spinner;
    @BindView(R.id.editTextPhone)
    EditText editText;

    @OnClick(R.id.buttonContinue)
    public void onClick(View v) {
        String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        String number = editText.getText().toString().trim();
        if (number.isEmpty() || number.length() < 10) {
            editText.setError("Valid number is required");
            editText.requestFocus();
            return;
        }

        String phoneNumber = "+" + code + number;
        Intent intent = new Intent(PhoneActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", phoneNumber);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_activity);
        ButterKnife.bind(this);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}