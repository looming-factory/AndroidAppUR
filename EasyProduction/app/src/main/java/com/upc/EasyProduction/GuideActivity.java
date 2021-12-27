package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * This class implements the GuidActivity.
 * @author Enric Lamarca Ferrés.
 */
public class GuideActivity extends AppCompatActivity {

    private TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }
}