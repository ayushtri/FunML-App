package com.celes.imgreco.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.celes.imgreco.R;
import com.celes.imgreco.helpers.BoxWithLabel;
import com.celes.imgreco.helpers.ImageHelperActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionActivity extends ImageHelperActivity {
    ObjectDetector objectDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification().build();
        objectDetector = ObjectDetection.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        InputImage inputImage =  InputImage.fromBitmap(bitmap, 0);
        objectDetector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                    @Override
                    public void onSuccess(List<DetectedObject> detectedObjects) {
                        if(!detectedObjects.isEmpty()){
                            StringBuilder builder = new StringBuilder();
                            List<BoxWithLabel> boxes = new ArrayList<>();
                            int countUnknowns = 0;
                            for(DetectedObject object : detectedObjects){
                                if(!object.getLabels().isEmpty()){
                                    String label = object.getLabels().get(0).getText();
                                    float conf = object.getLabels().get(0).getConfidence() * 100;
                                    builder.append(label).append(": ")
                                            .append(conf).append("\n");
                                    boxes.add(new BoxWithLabel(object.getBoundingBox(), label));
                                }
                                else{
                                    countUnknowns++;
                                }
                            }
                            if(countUnknowns>0) builder.append("No. of unknowns: ").append(countUnknowns).append("\n");
                            getOutputTextView().setText(builder.toString());
                            drawDetectionResult(boxes, bitmap);
                        }
                        else{
                            getOutputTextView().setText(R.string.cantDetect);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ObjectDetectionActivity.this, "not working", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
