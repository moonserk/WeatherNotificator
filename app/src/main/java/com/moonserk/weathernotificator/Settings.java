package com.moonserk.weathernotificator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.io.IOException;

public class Settings extends AppCompatActivity {

    private final int MAX_NUMBER_PICKER = 15;
    private final int MIN_NUMBER_PICKER = 0;

    private NumberPicker numberPicker;
    private EditText editCity;
    private Button applyButton;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        intent = new Intent(Settings.this, MainActivity.class);

        setupActionBar();
        initUI();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void initUI(){
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(MIN_NUMBER_PICKER);
        numberPicker.setMaxValue(MAX_NUMBER_PICKER);
        if(MainActivity.mSettings.contains(MainActivity.APP_SETTINGS_DIFFIRENCE)){
            numberPicker.setValue(MainActivity.mSettings.getInt(MainActivity.APP_SETTINGS_DIFFIRENCE, 10));
        }

        editCity = (EditText) findViewById(R.id.editCity);
        if(MainActivity.mSettings.contains(MainActivity.APP_SETTINGS_CITY)){
            editCity.setText(MainActivity.mSettings.getString(MainActivity.APP_SETTINGS_CITY , "Sarapul"));
        }

        applyButton = (Button) findViewById(R.id.button);


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = MainActivity.mSettings.edit();
                editor.putString(MainActivity.APP_SETTINGS_CITY, editCity.getText().toString());
                editor.putInt(MainActivity.APP_SETTINGS_DIFFIRENCE, numberPicker.getValue());
                editor.apply();
                startActivity(intent);
            }
        });
    }

}
