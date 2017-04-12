package com.example.gustavo.photostest.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gustavo.photostest.PhotosTestApplication;
import com.example.gustavo.photostest.R;
import com.example.gustavo.photostest.events.GetBinEvent;
import com.example.gustavo.photostest.managers.GetImageManager;
import com.example.gustavo.photostest.models.BinObject;
import com.example.gustavo.photostest.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.iv_image)
    public ImageView mIvImage;

    GoogleMap mGoogleMap;
    private ProgressDialog progressDialog;
    private boolean isMapReady = false;
    private String binId = "";
    private String binName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        binId = getIntent().getStringExtra(Constants.EXTRA_BIN_ID);
        binName = getIntent().getStringExtra(Constants.EXTRA_BIN_NAME);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(binName);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = ProgressDialog.show(this,"",getString(R.string.cargando));

        //cargar mapa
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        PhotosTestApplication.getBusProvider().register(this);
    }

    @Override
    public void onPause(){
        PhotosTestApplication.getBusProvider().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void binEvet(GetBinEvent event) {
        if(progressDialog != null);
            progressDialog.dismiss();

        if(event.isSuccess()){
            BinObject obj = event.getData();

            try {
                //convertir string que viene del servicio a JSONObject
                JSONObject jObj = new JSONObject(obj.getData());

                //obtener latitud y longitud del JSON que viene del servicio
                String latitud = jObj.getString("latitud");
                String longitud = jObj.getString("longitud");

                //decodificar imagen en string base64 a bitmap
                byte[] decodedString = Base64.decode(jObj.getString("imagen"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                mIvImage.setImageBitmap(decodedByte);

                try {
                    //generar punto en las coordenadas en el mapa
                    double double1 = Double.parseDouble(latitud);
                    double double2 = Double.parseDouble(longitud);

                    LatLng lg = new LatLng(double1, double2);

                    IconGenerator iconFactory = new IconGenerator(this);
                    iconFactory.setStyle(IconGenerator.STYLE_RED);
                    MarkerOptions point = new MarkerOptions()
                            .position(lg)
                            .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(binName)))
                            .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                    mGoogleMap.addMarker(point);

                    //acercar la camara en el mapa al punto
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(lg)
                            .zoom(15)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.map_fail, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            } catch (Throwable t) {
                Toast.makeText(this, R.string.file_fail, Toast.LENGTH_LONG).show();
                Log.e("My App", t.getMessage());
            }
        }
        else{
            Toast.makeText(this, R.string.get_data_fail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mGoogleMap = googleMap;

        //obtener datos de la api con el ID del bin
        GetImageManager.load(binId);
    }
}
