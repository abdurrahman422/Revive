package com.revive.medicinereminder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScannerActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private TextView textView;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        scanButton = findViewById(R.id.buttonScan);

        // ক্যামেরা থেকে ছবি তোলার জন্য বাটন ক্লিক হ্যান্ডলার
        scanButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Camera not supported", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // ক্যামেরা থেকে প্রাপ্ত ছবি
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            // ML Kit এর মাধ্যমে টেক্সট রেকগনিশন শুরু
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);

            // TextRecognition ব্যবহার করা হচ্ছে
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    .process(image)
                    .addOnSuccessListener(result -> {
                        StringBuilder extractedText = new StringBuilder();
                        // সকল টেক্সট ব্লক একত্রিত করা
                        for (Text.TextBlock block : result.getTextBlocks()) {
                            extractedText.append(block.getText()).append("\n");
                        }
                        // রেকগনাইজড টেক্সট UI তে দেখানো
                        textView.setText(extractedText.toString());
                    })
                    .addOnFailureListener(e -> {
                        // যদি টেক্সট রেকগনিশন ব্যর্থ হয়
                        Toast.makeText(this, "Failed to recognize text", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
