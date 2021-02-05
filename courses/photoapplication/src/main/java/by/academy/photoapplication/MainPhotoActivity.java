package by.academy.photoapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

enum FileStorage {
    INTERNAL, EXTERNAL
}

public class MainPhotoActivity extends AppCompatActivity {

    private static final String TAG = "tag";

    //    For checking manual permissions for API level 23
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 22;
    static final int CAPTURE_IMAGE_REQUEST = 16;

    private ImageView imageView;
    private FileStorage currentImageStorage = FileStorage.INTERNAL;
    private String mCurrentPhotoPath;
    private RadioGroup radioGroup;
    private TextView storageText;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(findViewById(R.id.toolbar));
        imageView = findViewById(R.id.imageView);
        storageText = findViewById(R.id.textViewStorage);
        radioGroup = findViewById(R.id.radioButtonStorage);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        Toast.makeText(getApplicationContext(), "Ничего не выбрано",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioButtonInternal:
                        currentImageStorage = FileStorage.INTERNAL;
                        Toast.makeText(getApplicationContext(), currentImageStorage.name(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioButtonExternal:
                        currentImageStorage = FileStorage.EXTERNAL;
                        Toast.makeText(getApplicationContext(), currentImageStorage.name(),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                storageText.setText(getFileStorageDir().getAbsolutePath());
            }
        });
        storageText.setText(getFileStorageDir().getAbsolutePath());
        setImage(imageView, readLastImageFileName());

        this.<FloatingActionButton>findViewById(R.id.fab).setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureImage();
            } else {
                displayMessage(getBaseContext(), " Capture Image function for 4.4.4 and lower is not supported");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void captureImage() {
        // check permission on storage and camera
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (checkPermission(this, permissions, MY_PERMISSIONS_REQUEST_CAMERA)) {
            Log.i(TAG, "requestPermissions " + Arrays.toString(permissions));
            captureImageCameraIfPermitted();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean checkPermission(final Activity context, String[] permission, int reqCode) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> necessaryPerms = Arrays.stream(permission).filter(
                p -> ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED
        ).collect(Collectors.toList());

        if (necessaryPerms.isEmpty()) {
            return true;
        }

        ActivityCompat.requestPermissions(context, necessaryPerms.toArray(new String[0]), reqCode);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: " + requestCode + "; grantResults: " + Arrays.toString(grantResults));
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            StringBuilder notGivenPermission = new StringBuilder();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    notGivenPermission.append(" ").append(permissions[i]);
                }
            }

            if (notGivenPermission.length() == 0) {
                captureImageCameraIfPermitted();
            } else {
                displayMessage(getBaseContext(), "Permissions not given: " + notGivenPermission.toString());
            }
        }
    }

    private void captureImageCameraIfPermitted() {
        Log.i(TAG, "captureImage: takePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //FIXME it  doesnt' work
//        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
//            displayMessage(getBaseContext(), "Null during resolveActivity method");
//            return;
//        }

        // Create the File where the photo should go
        try {
            File photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                Log.i(TAG, "Photo path:" + mCurrentPhotoPath);
                //          displayMessage(getBaseContext(), mCurrentPhotoPath);

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mydomain.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        } catch (Exception ex) {
            // Error occurred while creating the File
            displayMessage(getBaseContext(), ex.getMessage());
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getFileStorageDir();

        if (storageDir.exists() && !storageDir.isDirectory()) {
            boolean delete = storageDir.delete();
        }
        if (!storageDir.exists()) {
            boolean mkdir = storageDir.mkdir();
        }
        Log.i(TAG, "createImageFile: " + storageDir);
        Log.i(TAG, String.format("createImageFile %s %s ", storageDir.exists(), storageDir.canWrite()));
        File file = new File(storageDir, imageFileName);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
        }
        return file;
    }

    private File getFileStorageDir() {
        switch (currentImageStorage) {
            case EXTERNAL:
                return getExternalFilesDir("images");
            case INTERNAL:
        }
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = getFilesDir();
        return new File(getFilesDir(), "images");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Bundle extras = data.getExtras();
        //Bitmap imageBitmap = (Bitmap) extras.get("data");
        //imageView.setImageBitmap(imageBitmap);

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            setImage(imageView, mCurrentPhotoPath);
            writeLastImageFileName(mCurrentPhotoPath);
        } else {
            displayMessage(getBaseContext(), "Request cancelled or something went wrong.");
        }
    }

    private void setImage(ImageView imageView, String absolutePath) {
        Bitmap myBitmap = BitmapFactory.decodeFile(absolutePath);
        if (myBitmap != null) {
            int width = myBitmap.getWidth();
            int height = myBitmap.getHeight();
            Log.i(TAG, "absolutePath: " + absolutePath);
            Log.i(TAG, "image size: " + width + ";" + height);
            imageView.setImageBitmap(myBitmap);
        }
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String readLastImageFileName() {
        String filename = "lastImage";
        Log.i("files", "readLastImageFileName: reading text from file:" + filename);
        Context context = getApplicationContext();
        File file = new File(context.getFilesDir(), filename);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lastLine = bufferedReader.lines().
                    reduce((first, second) -> second)
                    .orElse(null);
            bufferedReader.close();
            Log.i("files", "readed:" + lastLine );
            return lastLine;
        } catch (IOException e) {
            System.out.println(e);
            Log.e("files", "error during work with file", e);
        }
        return null;
    }
    private void writeLastImageFileName(String mCurrentPhotoPath) {
        String filename = "lastImage";
        Log.i("files", "writeLastImageFileName: writing text '" +
                mCurrentPhotoPath +
                "' to file:" + filename);
        Context context = getApplicationContext();
        File file = new File(context.getFilesDir(), filename);
        try {
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(mCurrentPhotoPath + "\n");
            bufferWriter.close();
        } catch (IOException e) {
            System.out.println(e);
            Log.e("files", "error during work with file", e);
        }
    }
}