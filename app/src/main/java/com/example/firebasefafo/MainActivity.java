package com.example.firebasefafo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button selectFile, uploadFile, downloadFiles;
    private TextView notification;
    private Uri pdfUri; // are meant to hold path on local storage

    private FirebaseStorage storage; // used for uploading files
    private FirebaseDatabase database; // used to store URL's of uploaded files

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        downloadFiles = findViewById(R.id.download);
        selectFile = findViewById(R.id.select);
        notification = findViewById(R.id.notification);
        uploadFile = findViewById(R.id.upload);

        downloadFiles.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MyRecyclerViewActivity.class));
        });

        selectFile.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });

        uploadFile.setOnClickListener(view -> {
            if (pdfUri != null) {
                uploadFileToFireBase();
            } else {
                Toast.makeText(MainActivity.this, "select a file", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFileToFireBase() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("uploading file");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis() + "";
        System.out.println(">>>>>>>>>>>>>>>>>>> fileName " + fileName);
        System.out.println(">>>>>>>>>>>>>>>>>>> pdfUri " + pdfUri);
        StorageReference storageReference = storage.getReference(); // returns root path
        storageReference.child("Uploads").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Toast.makeText(MainActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
                                Log.d(">>>> TAG:", imageUrl);
                                DatabaseReference reference = database.getReference(); // return the path to root
                                reference.child(fileName).setValue(imageUrl).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "File uploaded successfully", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "File not successfully uploaded", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                    }
                });
//                .addOnSuccessListener(taskSnapshot -> {
//            String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(); // return the url for your uploaded file
//            System.out.println(">>>>>>>>>>>>>>>>>>> url " + url);
//            // save this url in realtime database
//            DatabaseReference reference = database.getReference(); // return the path to root
//            reference.child(fileName).setValue(url).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "File uploaded successfully", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "File not successfully uploaded", Toast.LENGTH_LONG).show();
//                }
//            });
//        }).addOnProgressListener(snapshot -> { progressDialog.setProgress((int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
//        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "File not uploaded", Toast.LENGTH_LONG).show());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else {
            Toast.makeText(MainActivity.this, "please provide permission", Toast.LENGTH_LONG).show();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File Please"), 19);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 19 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            String filePath = getRealPathFromURI(pdfUri);
            System.out.println(">>>>>>>>>>>>>>>>>>> selected file path " + filePath);
            notification.setText("file selected " + filePath);
        } else {
            Toast.makeText(MainActivity.this, "Please Select a file", Toast.LENGTH_LONG).show();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentURI, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
    }
}