package com.example.gustavo.photostest.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gustavo.photostest.BuildConfig;
import com.example.gustavo.photostest.PhotosTestApplication;
import com.example.gustavo.photostest.R;
import com.example.gustavo.photostest.events.PostBinEvent;
import com.example.gustavo.photostest.managers.UploadImageManager;
import com.example.gustavo.photostest.models.BinObject;
import com.example.gustavo.photostest.utils.ImageUtils;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.delight.android.location.SimpleLocation;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 0xbaba;
    private static final int REQUEST_ACCESS_CAMERA = 0xbabe;
    private static final int REQUEST_ACCESS_STORAGE = 0xbabf;
    private static final int REQUEST_FROM_CAMERA = 0xbabb;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.rl_photo)
    public RelativeLayout RlTakePhoto;

    @BindView(R.id.rl_list)
    public RelativeLayout RlSeePhotos;

    private Uri mUri;
    private String mCurrentPhotoPath;
    private ProgressDialog progressDialog;
    private SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        RlTakePhoto.setOnClickListener(this);
        RlSeePhotos.setOnClickListener(this);

        location = new SimpleLocation(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        PhotosTestApplication.getBusProvider().register(this);
    }

    @Override
    public void onPause(){
        PhotosTestApplication.getBusProvider().unregister(this);
        location.endUpdates();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == RlTakePhoto.getId()) {

            if(checkPermissions()){
                if (!location.hasLocationEnabled()) {
                    SimpleLocation.openSettings(this);
                    location.beginUpdates();
                } else {
                    location.beginUpdates();
                    takePhotoByCamera();
                }
            }
            else{
                Toast.makeText(this, R.string.all_permissions_needed, Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == RlSeePhotos.getId()) {
            Intent startIntent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(startIntent);
        }
    }

    public void takePhotoByCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile());
            } else {
                mUri = Uri.fromFile(createImageFilePrev());
            }
        }
        catch (IOException ex){
            Toast.makeText(this, R.string.file_fail, Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        intent.putExtra("return data", true);
        (this).startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    private File createImageFilePrev() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );

        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FROM_CAMERA && resultCode == RESULT_OK) {
            try {
                //codificar imagen a base64 string
                String encodedImage = "";

                Uri mImageUri = Uri.parse(mCurrentPhotoPath);
                File file = new File(mImageUri.getPath());
                try {
                    InputStream ims = new FileInputStream(file);

                    Bitmap bm = BitmapFactory.decodeStream(ims);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = ImageUtils.compressImage(file.toString());

                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, R.string.file_fail, Toast.LENGTH_LONG).show();
                    return;
                }

                /*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                else {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }*/

                //obtener coordenadas
                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                //crear json con los datos a subir
                JSONObject obj = new JSONObject();
                obj.put("imagen", encodedImage);
                obj.put("latitud", latitude);
                obj.put("longitud", longitude);

                BinObject bin = new BinObject();
                bin.setData(obj.toString());
                //subir datos
                progressDialog = ProgressDialog.show(this,"",getString(R.string.cargando));
                UploadImageManager.load(bin);

            } catch (Exception e) {
                Toast.makeText(this, R.string.file_fail, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private boolean checkPermissions() {
        if (!mayRequestCamera()) {
            return false;
        }
        if (!mayRequestStorage()) {
            return false;
        }
        if (!mayRequestLocation()) {
            return false;
        }
        return true;
    }

    private boolean mayRequestCamera() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CAMERA)) {
            requestPermissions(new String[]{CAMERA}, REQUEST_ACCESS_CAMERA);
        }
        else {
            requestPermissions(new String[]{CAMERA}, REQUEST_ACCESS_CAMERA);
        }

        return false;
    }

    private boolean mayRequestStorage() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_STORAGE);
        }
        else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_STORAGE);
        }

        return false;
    }

    private boolean mayRequestLocation() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!location.hasLocationEnabled()) {
                    SimpleLocation.openSettings(this);
                }
                checkPermissions();
            }
            else{
                Toast.makeText(this, R.string.all_permissions_needed, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_ACCESS_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            }
            else{
                Toast.makeText(this, R.string.all_permissions_needed, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_ACCESS_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            }
            else{
                Toast.makeText(this, R.string.all_permissions_needed, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Subscribe
    public void postEvet(PostBinEvent event) {
        if(progressDialog != null);
            progressDialog.dismiss();

        if(event.isSuccess()){
            Toast.makeText(this, R.string.save_data_success, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, R.string.save_data_fail, Toast.LENGTH_LONG).show();
        }
    }
}