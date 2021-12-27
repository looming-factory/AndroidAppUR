package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * This class implements the AboutActivity.
 * @author Enric Lamarca Ferrés.
 */
public class AboutActivity extends AppCompatActivity {

    private TextView aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();

        aboutText = findViewById(R.id.about_text);

        aboutText.setText("Final degree project by Enric Lamarca Ferrés\nUniversitat Politècnica de Catalunya (UPC)" +
                "\n\n" +
                "Implemented for a CB3-series robot\n" +
                "of Universal Robots\n" +
                "with URSoftware version 3.14");
    }
}