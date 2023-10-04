package com.celes.imgreco.helpers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celes.imgreco.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageHelperActivity extends AppCompatActivity {
    ImageView inputImageView;
    TextView outputTextView;
    Button pickImg, clickImg;
    ActivityResultLauncher<Intent> imageResultLauncher, captureResultLauncher;
    File photoFile;
    private static final int READ_IMG_PERM_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_helper);

        inputImageView = findViewById(R.id.imageViewInput);
        outputTextView = findViewById(R.id.textViewOutput);
        pickImg = findViewById(R.id.pickImgBtn);
        clickImg = findViewById(R.id.clickImgBtn);

        registerImageResultLauncher();

        pickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissionAndGetImage();
            }
        });

        registerCaptureResultLauncher();
        clickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFromCamera();
            }
        });
    }
    private void captureFromCamera(){
        photoFile = createPhotoFile();

        Uri fileUri = FileProvider.getUriForFile(ImageHelperActivity.this, "com.celes.imgreco.fileprovider",photoFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        captureResultLauncher.launch(intent);
    }
    private void registerCaptureResultLauncher() {
        captureResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            inputImageView.setImageBitmap(bitmap);
                            runClassification(bitmap);
                        }
                        catch (Exception exception){
                            exception.printStackTrace();
                            Toast.makeText(ImageHelperActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private File createPhotoFile(){
        File  photoFileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ML_IMAGE_HELPER");

        if(!photoFileDir.exists()) photoFileDir.mkdirs();

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(photoFileDir.getPath() + File.separator + name);
        return file;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imageResultLauncher.launch(intent);
    }
    private void registerImageResultLauncher() {
        imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Uri imageUri = result.getData().getData();
                            Bitmap bitmap = loadFromUri(imageUri);
                            inputImageView.setImageBitmap(bitmap);
                            runClassification(bitmap);
                        }
                        catch (Exception exception){
                            exception.getStackTrace();
                            Toast.makeText(ImageHelperActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private Bitmap loadFromUri(Uri uri){
        Bitmap bitmap;

        try{
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O_MR1){
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            else{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  bitmap;
    }
    private void checkStoragePermissionAndGetImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, READ_IMG_PERM_CODE);
            }
            else pickImageFromGallery();
        }
        else{
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_IMG_PERM_CODE);
            }
            else pickImageFromGallery();
        }
    }
    protected void runClassification(Bitmap bitmap){

    }
    protected TextView getOutputTextView(){
        return outputTextView;
    }
    protected ImageView getInputImageView(){
        return inputImageView;
    }

    protected void drawDetectionResult(List<BoxWithLabel> boxes, Bitmap bitmap){
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint penRect = new Paint();
        penRect.setColor(Color.RED);
        penRect.setStyle(Paint.Style.STROKE);
        penRect.setStrokeWidth(8f);

        Paint penLabel = new Paint();
        penLabel.setColor(Color.YELLOW);
        penLabel.setStyle(Paint.Style.FILL_AND_STROKE);
        penLabel.setTextSize(96f);
        penLabel.setStrokeWidth(2f);

        for(BoxWithLabel boxWithLabel : boxes){
            canvas.drawRect(boxWithLabel.rect, penRect);

            Rect labelSize = new Rect(0,0,0,0);
            penLabel.getTextBounds(boxWithLabel.label, 0,boxWithLabel.label.length(),labelSize);

            float fontSize = penLabel.getTextSize() * boxWithLabel.rect.width() / labelSize.width();
            if(fontSize < penLabel.getTextSize()){
                penLabel.setTextSize(fontSize);
            }
            canvas.drawText(boxWithLabel.label, boxWithLabel.rect.left, boxWithLabel.rect.top + labelSize.height(), penLabel);
            getInputImageView().setImageBitmap(outputBitmap);
        }
    }
}