package com.celes.imgreco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.celes.imgreco.image.FlowerIdentificationActivity;
import com.celes.imgreco.image.ImageClassificationActivity;
import com.celes.imgreco.image.ObjectDetectionActivity;

public class MainActivity extends AppCompatActivity {
    Button imgClassification, flowerClassification, objectDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgClassification = findViewById(R.id.imgClassificationBtn);
        flowerClassification = findViewById(R.id.flowerClassisfactionBtn);
        objectDetection = findViewById(R.id.objectDetectionBtn);

        imgClassification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageClassificationActivity.class);
                startActivity(intent);
            }
        });

        flowerClassification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlowerIdentificationActivity.class);
                startActivity(intent);
            }
        });

        objectDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });
    }
}